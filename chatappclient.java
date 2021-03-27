package com.chatapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class chatappclient {
    public static void main(String[] args) throws IOException { nameTaker(); }
    static Socket s;
    static String names;
    static DataInputStream din;
    static File r;
    static File NameIDRead;
    public static void nameTaker() throws IOException {
        r=new File("D://chat");
        if(r.isDirectory()){
            NameIDRead=new File("D://chat//NameId.txt");                                                        //TODO:in case of multiple user change destination here
            Scanner sc=new Scanner(NameIDRead);
            while (sc.hasNextLine()){
                names=sc.nextLine();
            }// username = Info
            try {
                s = new Socket("192.168.0.104", 9090);                                                           //TODO:in case of changing host
                din = new DataInputStream(s.getInputStream());
                dou = new DataOutputStream(s.getOutputStream());
                dou.writeUTF(names);
                dou.flush();
                ui.main();
                msg_reading ms = new msg_reading(din, dou);
                Thread t = new Thread(ms);
                t.start();
            } catch (Exception hg) {
                System.out.println(hg);
            }
        } else{
            JFrame f = new JFrame();
            f.setTitle("enter your name ");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(362, 90);
            f.setLocationRelativeTo(null);
            f.setResizable(false);
            f.setLayout(null);
            f.getContentPane().setBackground(Color.GRAY);
            JButton b = new JButton();
            b.setText("DONE");
            b.setBounds(250, 0, 100, 50);
            b.setFocusable(false);
            b.setBorder(BorderFactory.createEtchedBorder());
            b.setBackground(Color.GRAY);
            b.setCursor(new Cursor(12));
            JTextField j = new JTextField();
            j.setFont(new Font("Arial", Font.BOLD, 20));
            j.setCaretColor(Color.magenta);
            j.setForeground(Color.black);
            j.setCursor(new Cursor(Cursor.TEXT_CURSOR));
            j.setBounds(0, 0, 250, 50);
            String finalInfo = names;
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    File Folder = new File("D://chat");                                                          //TODO:in case of multiple user change destination here
                    System.out.println(Folder.mkdir());
                    File NameID = new File("D://chat//NameID.txt");                                              //TODO:in case of multiple user change destination here
                    try {
                        NameID.createNewFile();
                        // writing the user name and the uuid on the file
                        //using the file writer class
                        FileWriter NameIDWriter = new FileWriter("D://chat//NameID.txt");                        //TODO:in case of multiple user change destination here
                        // creating a new id for the user
                        NameIDWriter.write(j.getText());
                        NameIDWriter.close();
                    } catch (IOException i) {
                        System.out.println(i);
                    }
                    names=j.getText();
                    boolean hashTag = false;
                    boolean Space = false;
                    f.dispose();
                    String hj = j.getText();
                    for (int i = 0; i < hj.length(); i++) {
                        hashTag = hj.charAt(i) == '#';
                    }
                    for (int i = 0; i < hj.length(); i++) {
                        Space = hj.charAt(i) == ' ';
                    }
                    if (!j.getText().equals("")) {
                        if (!hashTag && !Space) {
                            try {
                                s = new Socket("192.168.0.104", 9090);                                            //TODO:in case of changing host
                                din = new DataInputStream(s.getInputStream());
                                dou = new DataOutputStream(s.getOutputStream());
                                dou.writeUTF(names);
                                dou.flush();
                                ui.main();
                                msg_reading ms = new msg_reading(din, dou);
                                Thread t = new Thread(ms);
                                t.start();
                            } catch (Exception hg) {
                                System.out.println(hg);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "No Space Or \n# Allowed In Name", "Failure", JOptionPane.ERROR_MESSAGE);
                            j.setText("");
                            f.show();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Your name \n Can't be \n Blank \n Or your name \n Cannot Include \"#\" ", "Failure", JOptionPane.ERROR_MESSAGE);
                        j.setText("");
                        f.show();
                    }
                }
            });
            f.add(j);
            f.add(b);
            f.setVisible(true);
        }
    }
    static DataOutputStream dou;
}
class msg_reading implements Runnable{
    private final DataInputStream din;
    private final DataOutputStream dou;
    static ArrayList<String> names_list = new ArrayList<>();
    static ArrayList<String> names_checker = new ArrayList<>();
    msg_reading(DataInputStream din,DataOutputStream dou){
        this.din=din;
        this.dou=dou;
    }

    @Override
    public void run() {
        String te;
        //string declaration
        while (true){
            try {
                te=din.readUTF();
                //reading the massage
                if (te.charAt(0) == '#' && te.charAt(1) == '#' && te.charAt(2) == '#' && te.charAt(3) == '#') {
                    te = te.replaceAll("#", "");
                    names_list nl = new names_list(te,names_list,names_checker);
                }
                else {
                    StringTokenizer st = new StringTokenizer(te, "#");
                    String username = st.nextToken();
                    //getting the username
                    String msg = st.nextToken();
                    //getting the massage
                    String recipient = st.nextToken();
                    //getting the recipient name
                    System.out.println(username + ">>> " + msg);
                    System.out.println(username);
                    for (addbutton cli : ui.buttoner) {
                        System.out.println(cli.name);
                        cli.name = cli.name.replaceAll(" ", "");
                        if (cli.name.equals(username)) {
                            System.out.println("@writer");
                            cli.texta.setLineWrap(true);
                            cli.texta.setFont(new Font("Arial", Font.PLAIN, 16));
                            cli.texta.setForeground(Color.white);
                            DateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
                            String dateString = dateFormat.format(new Date()).toString();
                            System.out.println("Current time in AM/PM: " + dateString);
                            cli.texta.setText(cli.texta.getText() + "\n" + "[" + dateString + "]" + cli.b.getText() + " :" + msg);
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        try {
            this.din.close();
            this.dou.close();
            //closing the resources
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class names_list {
    //reading clients for adding button
    private ArrayList<String> name_list=new ArrayList<>();
    private ArrayList<String> names_checker=new ArrayList<>();
    static boolean check;
    names_list(String te, ArrayList<String> names_list, ArrayList<String> names_checker){
        this.name_list = names_list;
        this.names_checker = names_checker;
        String rawnames;
        String stonames;
        String names;
        rawnames = te;
        stonames = rawnames.replace("[", "");
        names = stonames.replace("]", "");
        StringTokenizer st = new StringTokenizer(names, ",");
        while (st.hasMoreTokens()) {
            String n = st.nextToken();
            n = n.replace(" ", "");
            System.out.println(n);
            this.name_list.add(n);
            if (!String.valueOf(this.names_checker).equals("")) {
                System.out.println("@if1");
                for (int i = 0; i < this.name_list.size(); i++) {
                    //loop to add button
                    System.out.println("@for1");
                    if (this.name_list.size() >= this.names_checker.size()) {
                        //checking if more clients available
                        int size = this.name_list.size() - 1;
                        String more = this.name_list.get(size);
                        System.out.println(more);
                        //name to be added
                        System.out.println("if2");
                        System.out.println("@if2 " + this.names_checker);
                        boolean check = this.names_checker.contains(more);
                        System.out.println(check);
                        if (!more.equals(chatappclient.names) && !check) {
                            this.names_checker.add(more);
                            System.out.println("@works " + this.names_checker);
                            //adding name to permanent list
                            System.out.println("@works");
                            ui.addedr(more);
                            //calling the button adder class
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
        }
        this.name_list.clear();
    }
}

class ui
{
    static JButton b;
    static int count;
    static JFrame fra = new JFrame(chatappclient.names);
    static JTextArea tf=new JTextArea();
    static JScrollPane pane=new JScrollPane(tf);
    static JPanel tpane=new JPanel();
    static Vector<addbutton> buttoner=new Vector<>();
    public static void main() {
        count = 0;
        //y axis location of button
        //frame
        fra.setLayout(null);
        fra.setResizable(false);
        fra.setLocationRelativeTo(null);
        fra.setSize(814, 739);
        fra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //text area containing buttons
        tf.setEditable(false);
        tf.setBackground(new Color(23,23,23));
        tf.setBackground(Color.DARK_GRAY);
        //scroll pane containing tf
        pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        pane.setBounds(0, 0, 400, 700);
        pane.setBackground(Color.DARK_GRAY);

        tpane.setBackground(Color.DARK_GRAY);
        tpane.setBounds(400,0,400,700);
        tpane.setLayout(null);

        fra.add(pane);
        fra.add(tpane);
        fra.setVisible(true);
    }
    public static void addedr(String name){
        //button adding method
        b=new JButton();
        //button to be added
        b.setText(name);
        b.setBounds(0,count,400,60);
        b.setBackground(Color.LIGHT_GRAY);
        b.setFocusable(false);
        b.setCursor(new Cursor(12));
        b.setBorder(BorderFactory.createBevelBorder(1));
        JTextArea texta=new JTextArea();
        //textarea for the massages
        JTextField textf=new JTextField();
        //J-panel for the name of the client user is talking to
        JPanel UPanel=new JPanel();
        //JLabel for the name of the client to show in the panel
        JLabel CLabel=new JLabel();
        // jLabel for other things
        JLabel activeNow=new JLabel("ONLINE");
        JLabel back=new JLabel();
        JLabel mores=new JLabel();

        //textfield for writing the massage
        JButton sendbutton=new JButton("SEND");
        //buttton for sending the massage
        addbutton bth=new addbutton(b,texta,name,textf,sendbutton,UPanel,CLabel,activeNow,back,mores);
        buttoner.add(bth);
        //button adding class
        count+=60;
        //incrementing count
    }
}
class addbutton
{
    final JButton b;
    final JTextArea texta;
    String name;
    final JTextField textf;
    final JButton sendbutton;
    final JPanel UPanel;
    final JLabel CLabel;
    final JLabel activeNow;
    final JLabel back;
    final JLabel mores;

    public addbutton(JButton b,JTextArea texta,String name,JTextField textf, JButton sendbutton,JPanel UPanel,JLabel CLabel, JLabel activeNow,JLabel back,JLabel mores)
    {
        this.b=b;
        this.texta=texta;
        this.name =name;
        this.textf=textf;
        this.sendbutton=sendbutton;
        this.UPanel=UPanel;
        this.CLabel=CLabel;
        this.activeNow=activeNow;
        this.back=back;
        this.mores=mores;

        ui.tf.add(b);
        //adding to tf
        ui.tf.append("\n\n");
        //for scroll
        ui.pane.updateUI();
        //update the ui
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ui.tpane.removeAll();

                //removing all elements
                // editing the panel
                // 7 94 84
                UPanel.setBackground(new Color(18,140,126));
                UPanel.setLayout(null);
                UPanel.setBorder(BorderFactory.createEtchedBorder());
                UPanel.setBounds(0,0,400,60);
                //aditing the clabel
                CLabel.setText(name);
                CLabel.setBounds(80,0,100,36);
                CLabel.setFont(new Font("Comic Sans",Font.PLAIN,30));
                CLabel.setForeground(Color.orange);

                activeNow.setForeground(Color.RED);
                activeNow.setFont(new Font("Arial",Font.BOLD,14));
                activeNow.setBounds(82,38,100,18);

                ImageIcon i1=new ImageIcon(ClassLoader.getSystemResource("icons/Back_Arrow.png"));
                Image i2= i1.getImage().getScaledInstance(60,60,Image.SCALE_DEFAULT);
                ImageIcon backArrow=new ImageIcon(i2);
                back.setIcon(backArrow);
                back.setBounds(4,0,60,60);
                back.setCursor(new Cursor(12));

                ImageIcon l3=new ImageIcon(ClassLoader.getSystemResource("icons/3dots.png"));
                Image l4=l3.getImage().getScaledInstance(45,50,Image.SCALE_DEFAULT);
                ImageIcon dots_3=new ImageIcon(l4);

                mores.setIcon(dots_3);
                mores.setBounds(350,5,45,50);
                mores.setCursor(new Cursor(12));




                JScrollPane PAN =new JScrollPane(texta);
                PAN.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                PAN.setBounds(0,60,400,600);
                PAN.getVerticalScrollBar().setBackground(Color.black);
                PAN.getVerticalScrollBar().setBorder(BorderFactory.createEmptyBorder());
                PAN.getVerticalScrollBar().setUnitIncrement(6);
                texta.setEditable(false);
                texta.setForeground(Color.WHITE);
                texta.setLineWrap(true);
                texta.setFont(new Font("Arial",Font.PLAIN,20));
                //texta.setBounds(0,0,400,680);
                texta.setBorder(BorderFactory.createBevelBorder(1));
                texta.setBackground(Color.DARK_GRAY);
                textf.setBounds(0,660,320,40);
                textf.setFont(new Font("Arial",Font.PLAIN,18));
                textf.setText("Write A Message......");

                sendbutton.setBounds(320,660,80,40);
                sendbutton.setCursor(new Cursor(12));
                sendbutton.setFont(new Font("MV Boli",Font.BOLD,23));
                sendbutton.setFocusable(false);
                sendbutton.setForeground(Color.white);
                sendbutton.setBackground(Color.DARK_GRAY);
                sendbutton.setBorder(BorderFactory.createEtchedBorder());
                sendbutton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(!textf.getText().equals("")){
                            try {
                                DateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
                                String dateString = dateFormat.format(new Date()).toString();
                                texta.setText(texta.getText()+"\n"+"["+dateString+"]"+" You :"+textf.getText());
                                System.out.println(b.getText());
                                String recipient=b.getText();
                                //reading the recipient name
                                recipient=recipient.replace(" ","");
                                //replacing the gaps
                                System.out.println(recipient);
                                String massage=chatappclient.names+"#"+textf.getText()+"#"+recipient;
                                textf.setText("");
                                chatappclient.dou.writeUTF(massage);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }else {
                            JOptionPane optionPane = new JOptionPane("Can't Send \n Blank Message", JOptionPane.ERROR_MESSAGE);
                            JDialog dialog = optionPane.createDialog("Failure");
                            dialog.setAlwaysOnTop(true);
                            dialog.setVisible(true);
                        }
                    }
                });
                UPanel.add(mores);
                UPanel.add(back);
                UPanel.add(activeNow);
                UPanel.add(CLabel);
                ui.tpane.add(UPanel);
                ui.tpane.add(PAN);
                ui.tpane.add(textf);
                ui.tpane.add(sendbutton);
                ui.tpane.updateUI();
            }

        });
    }
}