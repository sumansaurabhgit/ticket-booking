package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.util.userServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserBookingServices {
    private User user;
    private List<User> userList;
    private ObjectMapper objectMapper=new ObjectMapper();
    private static final String USERS_PATH="app/src/main/java/ticket/booking/localDb/users.json";
    public File users=new File(USERS_PATH);

    public UserBookingServices(User user1)throws IOException {
        this.user=user1;
        loadUserListFromFile();
    }
    public UserBookingServices()throws IOException{
        loadUserListFromFile();
    }
    private void loadUserListFromFile()throws IOException{
        userList=objectMapper.readValue(users,new TypeReference<List<User>>(){});
    }

    public Boolean loginUser(){
        Optional<User> foundUser=userList.stream().filter(user1->{
            return user1.getName().equals(user.getName())&& userServiceUtil.checkPassword(user.getPassword(),user1.getHashedPassword());
        }).findFirst();
        return foundUser.isPresent();
    }
    public Boolean signUp(User user1){
        try{
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        }catch(IOException ex){
            return Boolean.FALSE;
        }
    }
    private void saveUserListToFile()throws IOException{
        File userFile=new File(USERS_PATH);
        objectMapper.writeValue(userFile,userList);
    }
    public void fetchBookings(){
        Optional<User> userFetched=userList.stream().filter(user1->{
            return user1.getName().equals(user.getName())&& userServiceUtil.checkPassword(user.getPassword(),user1.getHashedPassword());
        }).findFirst();
        if(userFetched.isPresent()){
            userFetched.get().printTickets();
        }
    }
    // cancel booked ticket

    public Boolean cancelBooking(String ticketId){
        Scanner s=new Scanner(System.in);
        System.out.println("Enter the ticket id to cancel");
        ticketId=s.next();

        if(ticketId==null || ticketId.isEmpty()){
            System.out.println("Ticket ID cannot be null or Empty");
            return Boolean.FALSE;
        }
        String finalTicketId=ticketId;
        boolean removed=user.getTicketBooked().removeIf(ticket-> ticket.getTicketId().equals(finalTicketId));
        if(removed){
            System.out.println("Ticket with ID"+ticketId+"has been canceled");
            return Boolean.TRUE;
        }else{
            System.out.println("No ticket found with ID"+ticketId);
            return Boolean.FALSE;
        }
    }
    public List<Train> getTrains(String source, String destination){
        try{
            TrainServices trainServices=new TrainServices();
            return trainServices.searchTrains(source,destination);
        }catch(IOException ex){
            return new ArrayList<>();
        }
    }
    public List<List<Integer>>fetchSeats(Train train){
        return train.getSeats();
    }
    public Boolean bookTrainSeats(Train train,int row,int seat){
        try{
            TrainServices trainServices=new TrainServices();
            List<List<Integer>>seats=train.getSeats();
            if(row>=0 && row<seats.size() && seat>=0 && seat<seats.get(row).size()){
                if(seats.get(row).get(seat)==0){
                    seats.get(row).set(seat,1);
                    train.setSeats(seats);
                    trainServices.addTrain(train);
                    return true;
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }catch(IOException ex){
            return Boolean.FALSE;
        }
    }
}
