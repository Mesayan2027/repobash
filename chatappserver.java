package com.chatapp;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

public class chatappserver {
    static int sizeDefault=24;
    static JFrame frame = new JFrame();
    static JTextArea area = new JTextArea();
    static JScrollPane PAN = new JScrollPane(area);
    static Vector<Clienthandler> client_list = new Vector<>();
    static ArrayList<String> names_list = new ArrayList<>();

    public static void main(String[] args) {
        UI();
        //calling the ui class
    }
    public static void UI() {
        frame.setSize(1000, 800);
        frame.setTitle("Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        PAN.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        area.setFont(new Font("Calibri", Font.PLAIN, 24));
        area.setForeground(Color.green);
        area.setBackground(Color.black);
        area.setCaretColor(Color.magenta);
        area.setLineWrap(true);
        DateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
        String dateString = dateFormat.format(new Date()).toString();
        String w=dateString+"\n"+"Hello Admin\nThis is your Server \nEnjoy :D";
        area.setText(w);
        area.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    String s = area.getText();
                    String []LineCommand=s.split("\\n");
                    int lastLine=LineCommand.length-1;
                    switch (LineCommand[lastLine]) {
                        case "Enjoy :D":
                        case  "for help enter /help":
                        case "":
                            break;
                        case "/exit":
                            System.exit(101);
                        case "/start":
                            try {
                                server();
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                                area.setText(area.getText()+"\n"+"Problem Occurred");
                            }
                            break;
                        case "/help":
                            area.setText(area.getText() + "\n" + "To exit = /exit\nTo start server = /start\nTo stop server = /stop\nTo Clear all texts = /clear");
                            break;
                        case "/stop":
                            break;
                        case "/clear":
                            area.setText("");
                            break;
                        default:
                            area.setText(area.getText() + "\n" + "for help enter /help");
                            break;
                    }
                }
            }
        });
        area.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if( e.getKeyChar()=='-' ){
                    area.setFont(new Font("Calibri",Font.PLAIN,sizeDefault--));
                    String a=area.getText();
                    area.setText(a.replace("-",""));
                }else if(e.getKeyChar()=='+'){
                    String a=area.getText();
                    area.setText(a.replace("+",""));
                    area.setFont(new Font("Calibri",Font.PLAIN,sizeDefault++));
                }
            }
        });


        frame.add(PAN);
        frame.setVisible(true);


    }
    public static void server() throws IOException {
        ServerSocket ss = new ServerSocket(9090);
        //server socket
        Socket s;
        //socket declaration

        while (true) {
            s = ss.accept();
            //accepting client
            DataInputStream din = new DataInputStream(s.getInputStream());
            //data input steam
            DataOutputStream dou = new DataOutputStream(s.getOutputStream());
            //data output stream
            boolean logon = true;
            //logon state
            String name = din.readUTF();
            //reading the client name
            Clienthandler ch = new Clienthandler(din, dou, true, name);
            //object
            Thread t = new Thread(ch);
            //creating the thread with the object
            t.start();
            //starting the thread

            client_list.add(ch);
            //adding to the object to client_list
            names_list.add(name);
            //adding names to names_list
        }
    }
}
class Clienthandler implements Runnable {
    private final DataInputStream din;
    private final DataOutputStream dou;
    private boolean logon;
    private final String name;

    Clienthandler(DataInputStream din, DataOutputStream dou, boolean logon, String name) {
        this.din = din;
        this.dou = dou;
        this.logon = logon;
        this.name = name;
    }

    @Override
    public void run() {
        String te;
        String namel="####"+String.valueOf(chatappserver.names_list);
        System.out.println(namel);
        //declaration
        for (Clienthandler mc : chatappserver.client_list) {
            //cycling through the clients vector for sending the massage to everyone who is logged on
            if (mc.logon) {
                try {
                    mc.dou.writeUTF(namel);
                    mc.dou.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        while (true) {
            try {
                te = din.readUTF();
                //reading the massage
                StringTokenizer st = new StringTokenizer(te, "#");
                //Tokenizer
                String username = st.nextToken();
                //getting the username
                String msg = st.nextToken();
                //getting the massage
                String recipient = st.nextToken();
                //getting the recipient name
                System.out.println(msg);
                chatappserver.area.setText(chatappserver.area.getText()+"\n"+msg);
                for (Clienthandler mc : chatappserver.client_list) {
                    //cycling through the clients vector for sending the massage to everyone who is logged on
                    if (mc.logon && mc.name.equals(recipient)) {
                        mc.dou.writeUTF(te);
                        //writing the massage to the client
                        dou.flush();
                    } else if (!mc.logon) {
                        chatappserver.client_list.remove(mc);
                        //removing the not log-on clients
                    }
                }
            } catch (IOException e) {
                chatappserver.area.setText(chatappserver.area.getText()+"\n"+e);
                break;
            }
        }
        try {
            //client disconnects here
            this.din.close();
            this.dou.close();
            this.logon = false;
            //closing everything up
        } catch (IOException e) {
            chatappserver.area.setText(chatappserver.area.getText()+"\n"+e);
        }
    }
}