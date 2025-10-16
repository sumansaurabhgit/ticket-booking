package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Train;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class TrainServices {
     private List<Train> trainList;
     private ObjectMapper objectMapper=new ObjectMapper();
     private static final String TRAIN_DB_PATH="../localDb/train.json";
     public TrainServices()throws IOException {
         File trains=new File(TRAIN_DB_PATH);
         trainsList=objectMapper.readValue(trains, new TypeReference<List<Train>>() {
         });
     }
     public List<Train> searchTrains(String source,String destination){
         return trainList.stream().filter(train->validTrain(train,source,destination)).collect(collectors.toList());
     }
     private boolean validTrain(Train train,String source,String destination){
         List<string>stationOrder=train.getStations();
         int sourceIndex=stationOrder.indexOf(source.toLowerCase());
         int destinationIndex=stationOrder.indexOf(destination.toLowerCase());
         return sourceIndex != -1 && destinationIndex !=-1 && sourceIndex<destinationIndex;
     }
    //adding new Train
     public void addTrain(Train newTrain){
         Optional<Train> existingTrain=trainList.stream().filter(train->train.getTrainId()
                 .equalsIgnoreCase(newTrain.getTrainId())).findFirst();
         if(existingTrain.isPresent()){
             updateTrain(newTrain);
         }else{
             trainList.add(newTrain);
             saveTrainListToFile();
         }
     }
     public void updateTrain(Train updatedTrain){
         OptinalInt index= IntStream.range(0,trainList.size()).filter(i->trainList.get(i).getTrainId()
                 .equalsIgnoreCase(updatedTrain.getTrainId())).findFirst();
         if(index.isPresent()){
             trainList.set(index.getAsInt(),updatedTrain);
             saveTrainListToFile();
         }else{
             addTrain(updatedTrain);
         }

    }
    private void saveTrainListToFile(){
         try{
             objectMapper.writeValue(new File(TRAIN_DB_PATH),trainList);
         }catch(IOException e){
             e.printStackTrace();
         }
    }

}
