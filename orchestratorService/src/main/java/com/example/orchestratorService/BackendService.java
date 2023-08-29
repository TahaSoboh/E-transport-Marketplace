package com.example.orchestratorService;

import org.json.JSONException;
import org.json.simple.parser.*;
import org.json.JSONArray;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import org.json.simple.JSONObject;

@RestController
@CrossOrigin(origins = "*")
public class BackendService {


    public String getid() throws IOException {
        URL urladdress = new URL("https://www.random.org/strings/?num=1&len=8&digits=on&upperalpha=on&loweralpha=on&unique=on&format=plain&rnd=new");
        String readLine = null;
        //System.out.println(auth);
        //System.out.println(authorization);
        HttpURLConnection conection = (HttpURLConnection) urladdress.openConnection();
        conection.setRequestMethod("GET");
        boolean isassigned = false;
        int responseCode = conection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conection.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((readLine = in.readLine()) != null) {
                response.append(readLine);
            }
            in.close();
            System.out.println(response);
            return response.toString();
        }
        return "Null";
    }
    @PostMapping("/registerUser")
    public String registerNewUser(@RequestParam("Username") String username, @RequestParam("password") String password) throws IOException, ParseException {
        JSONObject newUSer = new JSONObject();
        File usersFile = new File("users.json");
        if(usersFile.exists()){
            System.out.println("File exist");
            Object obj = new JSONParser().parse(new FileReader("users.json"));
            org.json.JSONObject allUsers = new org.json.JSONObject(obj.toString());
            JSONArray usernames = allUsers.getJSONArray("usesrnames");
            usernames.put(username);
            newUSer.put("username", username);
            newUSer.put("password", password);
            allUsers.put(username, newUSer);
            allUsers.put("usesrnames", usernames);
            FileWriter file = new FileWriter("users.json");
            file.write((allUsers.toString()));
            file.close();
            JSONObject status = new JSONObject();
            status.put("Status", "200");
            return status.toString();
        }
        else {
            JSONObject allUsers = new JSONObject();
            JSONArray usernames = new JSONArray();
            usernames.put(username);
            newUSer.put("username", username);
            newUSer.put("password", password);
            allUsers.put(username, newUSer);
            allUsers.put("usesrnames", usernames);
            FileWriter file = new FileWriter("users.json");
            file.write(allUsers.toString());
            file.close();
            JSONObject status = new JSONObject();
            status.put("Status", "200");
            return status.toString();
        }

    }
    @PostMapping("/loginuser")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password) throws IOException, ParseException {
        if(isuservalid(username, password)){
            JSONObject status = new JSONObject();
            status.put("Status", "200");
            status.put("Name", username);
            return status.toString();
        }
        else {
            JSONObject status = new JSONObject();
            status.put("Status", "401");
            return status.toString();
        }

    }
    @PostMapping("/registerNewTrip")
    public Object registerTrip(@RequestParam("user") String user, @RequestParam("location") String location, @RequestParam("from") String from, @RequestParam("till") String till) throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader("users.json"));
        JSONObject allUsers = (JSONObject) obj;
        JSONObject myuser = (JSONObject) allUsers.get(user);
        System.out.println(till);
        try{
            org.json.simple.JSONArray mytrips = (org.json.simple.JSONArray) myuser.get("mytrips");
            JSONObject mytrip = new JSONObject();
            mytrip.put("location", location);
            mytrip.put("from", from);
            mytrip.put("till", till);
            String id = getid();

            mytrip.put("id", id);
            mytrips.add(mytrip);

        }
        catch (Exception exe){
            JSONArray mytrips = new JSONArray();
            JSONObject mytrip = new JSONObject();
            mytrip.put("location", location);
            mytrip.put("from", from);
            mytrip.put("till", till);
            String id = getid();
            mytrip.put("id", id);
            mytrips.put(mytrip);
            myuser.put("mytrips", mytrips);

        }
        FileWriter file = new FileWriter("users.json");
        file.write(allUsers.toString());
        file.close();
        JSONObject status = new JSONObject();
        status.put("Status", "200");
        return status.toString();


    }
    @PostMapping("/mytrips")
    public Object getalltrips(@RequestParam("username")String username) throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader("users.json"));
        org.json.JSONObject allUsers = new org.json.JSONObject(obj.toString());
        org.json.JSONObject status = new org.json.JSONObject();
        org.json.JSONObject myuser = allUsers.getJSONObject(username);
        try {
            JSONArray mytrips = myuser.getJSONArray("mytrips");
            status.put("Status", 200);
            status.put("trips", mytrips);
        }
        catch (Exception ex){
            status.put("Status", 404);
        }
        return status.toString();
    }
    @PostMapping("/myNotifications")
    public Object allNotifications(@RequestParam("username")String username) throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader("users.json"));
        org.json.JSONObject allUsers = new org.json.JSONObject(obj.toString());
        org.json.JSONObject status = new org.json.JSONObject();
        org.json.JSONObject myuser = allUsers.getJSONObject(username);
        try {
            JSONArray notifications = myuser.getJSONArray("notifications");
            status.put("Status", 200);
            status.put("notifications", notifications);
        } catch (Exception ex) {
            status.put("Status", 404);
        }
        return status.toString();
    }
    @PostMapping("/allusers")
    public Object getallusers(@RequestParam("username")String username) throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader("users.json"));
        org.json.JSONObject allUsers = new org.json.JSONObject(obj.toString());
        org.json.JSONObject status = new org.json.JSONObject();
        try {
            JSONArray mytrips = allUsers.getJSONArray("usesrnames");
            status.put("Status", 200);
            status.put("users", mytrips);
        }
        catch (Exception ex){
            status.put("Status", 404);
        }
        return status.toString();
    }

    @PostMapping("/sendNewNotification")
    public Object sendNotifiaction(@RequestParam("users")String users, @RequestParam("tripid") String tripid, @RequestParam("username") String currentuser) throws IOException, ParseException {
        org.json.JSONObject status = new org.json.JSONObject();
        String[] usersarray = users.split(",");
        try {
            addNotification(usersarray, currentuser, tripid);
            status.put("Status", 200);
        }
        catch (Exception ex){
            status.put("Status", 404);
        }
        return status.toString();
    }

    public void addNotification(String[] usernames, String currentuser, String tripId) throws IOException, ParseException {

        for(String username:usernames) {
            Object obj = new JSONParser().parse(new FileReader("users.json"));
            JSONObject allUsers = (JSONObject) obj;
            JSONObject myuser = (JSONObject) allUsers.get(username);
            org.json.JSONObject alltrips = new org.json.JSONObject(getalltrips(currentuser).toString());
            try {
                org.json.simple.JSONArray notifications = (org.json.simple.JSONArray) myuser.get("notifications");
                org.json.JSONArray trpisArray = alltrips.getJSONArray("trips");
                for(int j=0;j<trpisArray.length();j++){
                    System.out.println(trpisArray.getJSONObject(j).getString("id"));
                    if(trpisArray.getJSONObject(j).getString("id").equals(tripId)){
                        org.json.JSONObject mynotification = (org.json.JSONObject) trpisArray.get(j);
                        String notificationData = "Mr/miss "+currentuser+" invited you to join, on trip to "+ mynotification.getString("location") +" at " +mynotification.getString("from")+" till "+mynotification.getString("till");
                        notifications.add(notificationData);
                        myuser.put("notifications", notifications);
                    }
                }

                FileWriter file = new FileWriter("users.json");
                file.write(allUsers.toString());
                file.close();

            } catch (Exception exe) {
                org.json.JSONArray notifications = new org.json.JSONArray();
                org.json.JSONArray trpisArray = alltrips.getJSONArray("trips");
                for(int i=0;i<trpisArray.length();i++){
                    if(trpisArray.getJSONObject(i).getString("id").equals(tripId)){
                        org.json.JSONObject mynotification = (org.json.JSONObject) trpisArray.get(i);
                        String notificationData = "Mr/miss "+currentuser+" invited you to join, on trip to "+ mynotification.getString("location") +" at " +mynotification.getString("from")+" till "+mynotification.getString("till");
                        notifications.put(notificationData);
                        myuser.put("notifications", notifications);
                    }
                }
                FileWriter file = new FileWriter("users.json");
                file.write(allUsers.toString());
                file.close();

            }
        }

    }

    @PostMapping("/Weather")
    public String getWeather(@RequestParam("cityname") String city) throws IOException, ParseException {
        String readLine = null;
        URL urladdress = new URL("https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=e77c84e99fe7f5555fc62fdc38894de1");
        HttpURLConnection conection = (HttpURLConnection) urladdress.openConnection();
        conection.setRequestMethod("GET");
        int responseCode = conection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conection.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((readLine = in.readLine()) != null) {
                response.append(readLine);
            }
            in.close();
            org.json.JSONObject structuredData = setupData(response.toString());
            System.out.println(structuredData.toString());
            return structuredData.toString();
        }
        return "Null";

    }
    public org.json.JSONObject setupData(String rawData) throws ParseException {
        org.json.JSONObject myData = new org.json.JSONObject(rawData);
        int temp_celcius =myData.getJSONObject("main").getInt("temp") -273;
        int temp_min =myData.getJSONObject("main").getInt("temp_min") -273;
        int temp_max =myData.getJSONObject("main").getInt("temp_max") -273;
        int feels_like =myData.getJSONObject("main").getInt("feels_like") -273;
        int humidity =myData.getJSONObject("main").getInt("humidity");
        org.json.JSONObject structuredData = new org.json.JSONObject();
        structuredData.put("temp_celcius", temp_celcius);
        structuredData.put("temp_min", temp_min);
        structuredData.put("temp_max", temp_max);
        structuredData.put("feels_like", feels_like);
        structuredData.put("humidity", humidity);
        structuredData.put("name", myData.getString("name"));
        structuredData.put("Status", 200);
        return structuredData;


    }
    public boolean isuservalid(String username, String password) throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader("users.json"));
        JSONObject allUsers = (JSONObject) obj;
        JSONObject myuser = (JSONObject) allUsers.get(username);
        if(myuser.get("password").toString().equals(password)){
            return true;
        }
        else {
            return false;
        }
    }
}
