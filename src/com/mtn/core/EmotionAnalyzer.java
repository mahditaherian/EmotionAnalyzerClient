package com.mtn.core;

import com.mtn.entity.Accelerometer;
import com.mtn.entity.GpsLocation;
import com.mtn.entity.MagneticField;
import com.mtn.entity.Orientation;
import com.mtn.messages.*;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * @author Mahdi Taherian
 */
public class EmotionAnalyzer {
    static boolean USE_GPS = false;

    static Classifier classifier;
    static AccelerometerMessage tempAcc = null;
    static MagneticFieldMessage tempMg = null;
    static GpsLocationMessage tempGps = null;
    static OrientationMessage tempOri = null;
    static File dataFile = new File("data/data.arff");
    static File modelFile = new File("data/model.arff");

    static {
        learn();
    }

    //    public static String classify(double longitude, double latitude, double deltax, double deltay, double acceleration, double time) throws Exception {
    public static String classify(double acc_gx, double acc_gy, double acc_gz, double mag_x, double mag_y, double mag_z, double ori_x, double ori_y, double ori_z, double longitude, double latitude) throws Exception {
        ArffLoader loader = new ArffLoader();
        loader.setFile(modelFile);
        Instances instances = loader.getDataSet();
        instances.setClassIndex(instances.numAttributes() - 1);

        Instance inst = instances.instance(0);
        inst.setValue(0, acc_gx);
        inst.setValue(1, acc_gy);
        inst.setValue(2, acc_gz);

        inst.setValue(3, mag_x);
        inst.setValue(4, mag_y);
        inst.setValue(5, mag_z);

        inst.setValue(6, ori_x);
        inst.setValue(7, ori_y);
        inst.setValue(8, ori_z);

        inst.setValue(9, longitude);
        inst.setValue(10, latitude);

        inst.setDataset(instances);
        double val = classifier.classifyInstance(inst);
        return instances.classAttribute().value((int) val);
    }

    public static void learn() {
        try {
            ArffLoader loader = new ArffLoader();
            loader.setFile(dataFile);
            Instances instances = loader.getStructure();

            instances.setClassIndex(instances.numAttributes() - 1);

            // train NaiveBayes
            NaiveBayesUpdateable nb = new NaiveBayesUpdateable();
            nb.buildClassifier(instances);
            Instance current;
            while ((current = loader.getNextInstance(instances)) != null)
                nb.updateClassifier(current);
            classifier = nb;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
//        learn();
        createFile(modelFile, true);
        EmotionAnalyzer emotionAnalyzer = new EmotionAnalyzer();
        System.out.println("Start 10 second listening.....");
        emotionAnalyzer.receiveMessages(15000);
        System.out.println("End of listening.");
        System.out.println("Start classifying....");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        try {
            //FileReader fileReader = new FileReader(modelFile);
            ArffLoader loader = new ArffLoader();
            loader.setFile(modelFile);
            Instances dataSet = loader.getDataSet();
            System.out.println(classify(dataSet.instance(dataSet.numInstances() - 1)));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String classify(Instance instance) {
        try {

            return classify(
                    instance.value(0),
                    instance.value(1),
                    instance.value(2),
                    instance.value(3),
                    instance.value(4),
                    instance.value(5),
                    instance.value(6),
                    instance.value(7),
                    instance.value(8),
                    instance.value(9),
                    instance.value(10));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Unknown";
    }

    public void receiveMessages(int receivePeriod) {
        try {
            ServerSocket welcomeSocket = new ServerSocket(21211);
            long time = System.currentTimeMillis();
            while (System.currentTimeMillis() - time < receivePeriod) {
                // Create the Client Socket
                Socket clientSocket = welcomeSocket.accept();
                //System.out.println("##############################START##############################");
                // Create input and output streams to client
                ObjectInputStream inFromClient = new ObjectInputStream(clientSocket.getInputStream());

                // Read modify
                // TODO here

            /* Create Message object and retrive information */
                List<SensorMessage> messages;
                messages = (List<SensorMessage>) inFromClient.readObject();
                writeIntoFile(messages);


                //System.out.println("###############################END###############################");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeIntoFile(List<SensorMessage> messages) throws IOException {
        boolean updated = false;
        for (SensorMessage message : messages) {
            if (message instanceof AccelerometerMessage) {
                if (shouldReplace(tempAcc, message)) {
                    tempAcc = (AccelerometerMessage) message;
                    updated = true;
                }
            } else if (message instanceof OrientationMessage) {
                if (shouldReplace(tempOri, message)) {
                    tempOri = (OrientationMessage) message;
                    updated = true;
                }
            } else if (message instanceof GpsLocationMessage) {
                if (shouldReplace(tempGps, message)) {
                    tempGps = (GpsLocationMessage) message;
                    updated = true;
                }
            } else if (message instanceof MagneticFieldMessage) {
                if (shouldReplace(tempMg, message)) {
                    tempMg = (MagneticFieldMessage) message;
                    updated = true;
                }
            }

            System.out.println(message);
        }
        if (updated && tempMg != null && tempAcc != null & tempOri != null && USE_GPS && tempGps != null) {

            FileWriter writer = new FileWriter(modelFile, true);
            Accelerometer accelerometer = (Accelerometer) tempAcc.getEntity();
            MagneticField magneticField = (MagneticField) tempMg.getEntity();
            Orientation orientation = (Orientation) tempOri.getEntity();
            GpsLocation gpsLocation = null;
            if (USE_GPS) {
                gpsLocation = (GpsLocation) tempGps.getEntity();
            }
            writer.append(String.valueOf(accelerometer.getGx())).append(",").append(String.valueOf(accelerometer.getGy())).append(",").append(String.valueOf(accelerometer.getGz()));
            writer.append(",");
            writer.append(String.valueOf(magneticField.getX())).append(",").append(String.valueOf(magneticField.getY())).append(",").append(String.valueOf(magneticField.getZ()));
            writer.append(",");
            writer.append(String.valueOf(orientation.getX())).append(",").append(String.valueOf(orientation.getY())).append(",").append(String.valueOf(orientation.getZ()));
            writer.append(",");
            if (USE_GPS && gpsLocation != null) {
                writer.append(String.valueOf(gpsLocation.getLongitude())).append(",").append(String.valueOf(gpsLocation.getLatitude()));
            } else {
                writer.append("0,0");//instead of top line
            }
            writer.append(",'Unknown'");
            writer.append("\r\n");
            writer.flush();
            writer.close();
        }
    }

    public static void createFile(File file, boolean clearIfExist) {
        if (file.exists()) {
            if (clearIfExist) {
                file.delete();
            } else {
                return;
            }
        }

        try {
            file.createNewFile();

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.append("@relation weka");
            fileWriter.append("\r\n\r\n");
            fileWriter.append("@attribute acc_gx numeric").append("\r\n");
            fileWriter.append("@attribute acc_gy numeric").append("\r\n");
            fileWriter.append("@attribute acc_gz numeric").append("\r\n");
            fileWriter.append("@attribute mag_x numeric").append("\r\n");
            fileWriter.append("@attribute mag_y numeric").append("\r\n");
            fileWriter.append("@attribute mag_z numeric").append("\r\n");
            fileWriter.append("@attribute ori_x numeric").append("\r\n");
            fileWriter.append("@attribute ori_y numeric").append("\r\n");
            fileWriter.append("@attribute ori_z numeric").append("\r\n");
            fileWriter.append("@attribute longitude numeric").append("\r\n");
            fileWriter.append("@attribute latitude numeric").append("\r\n");
            fileWriter.append("@attribute state {' study',' walk',' eat',' run','  drive','Unknown'}");
            fileWriter.append("\r\n\r\n");
            fileWriter.append("@data").append("\r\n");

            fileWriter.flush();
            fileWriter.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean shouldReplace(SensorMessage src, SensorMessage target) {
        return src == null || src.getTime() < target.getTime();

    }


}
