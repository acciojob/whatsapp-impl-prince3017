package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String createUser(String name, String mobile) throws Exception{
        if(userMobile.contains(mobile)){
            throw new Exception("User already exists");
        }

        userMobile.add(mobile);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users){
        Group newGroup;
        if(users.size()==2){
            newGroup = new Group(users.get(1).getName(), users.size());
        }else{
            newGroup = new Group("Group"+Integer.toString(++customGroupCount), users.size());
        }
        groupUserMap.put(newGroup, users);
        adminMap.put(newGroup, users.get(0));
        return  newGroup;
    }

    public int createMessage(String content){
        Message newMessage = new Message(++messageId, content, new Date());
        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{
        if(!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        for(User user: groupUserMap.get(group)){
            if(user.getMobile() == sender.getMobile()){
                if(groupMessageMap.containsKey(group)){
                    groupMessageMap.get(group).add(message);
                }else{
                    List<Message> temp = new ArrayList<>();
                    temp.add(message);
                    groupMessageMap.put(group, temp);
                }
                return message.getId();

            }
        }
        throw new Exception("sender is not a member of the group");
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{
        if(!groupUserMap.containsKey(group)){
            throw new Exception("group does not exist");
        }
        if(adminMap.get(group) != approver){
            throw new Exception(" Approver is not the current admin of the group");
        }
        for(User u: groupUserMap.get(group)){
            if(u==user){
                adminMap.put(group, user);
                return "SUCCESS";
            }
        }
        throw new Exception("user is not a part of the group");
    }

}