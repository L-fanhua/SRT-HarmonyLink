import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
public class srtadminprotal {
    

    public static void main(String[] args) throws IOException {
        nobug();
        int PORT = 1028;
        if(args.length > 0 && args[0] != null && !args[0].isEmpty()) {
            PORT = Integer.parseInt(args[0]);
            //System.out.println(args[0]);
        }
        ServerSocket serverSocket = new ServerSocket(PORT);
        MyLogger.Log("Server started on port " + PORT);
        Thread order = new Thread(new Order());
        order.start();

        while (true) {
            Socket clientSocket = serverSocket.accept();
            MyLogger.Log("New client connected: " + clientSocket.getInetAddress().getHostName());

            Thread t = new Thread(new ClientHandler(clientSocket));
            t.start();
        }
    }
    
    public static class InviteCode {
        private static  List<String> inviteCodes = new ArrayList<>();
        public static String randomer() {
            Random random = new Random();
             
            StringBuilder stringBuilder = new StringBuilder();
            

            for (int i = 0; i < 20; i++) {
                int digit = random.nextInt(10);
                stringBuilder.append(digit);
            }

            String randomNumber = stringBuilder.toString();
            return randomNumber;
        }
        public static void Create() {
            String inviteCode = randomer();
            inviteCodes.add(inviteCode);
            MyLogger.Log("生成邀请码：" + inviteCode);
        }
        public static boolean check(String input) {
            if (inviteCodes.contains(input)) {
                inviteCodes.remove(input);
                System.out.println("邀请码有效");
                return true;
            } else {
                System.out.println("邀请码无效");
                return false;
            }
        }
        
        
    }
    
    public static class MyLogger {

        public static void Log(String log) {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String time = sdf.format(date);
            String timelog = "[" +
                                   time.substring(0, 4) + ":" +
                                   time.substring(4, 6) + ":" +
                                   time.substring(6, 8) + ":" +
                                   time.substring(8,10) + ":" +
                                   time.substring(10,12) + "] "+ log;
            System.out.println(timelog);
            try {
                FileWriter fileWriter = new FileWriter(new File("log.txt"), true);
                       
                fileWriter.write(timelog + "\n"); // 添加换行符
                fileWriter.close();
            
            } catch (IOException e) {
                System.out.println("写入文件时出现错误：" + e.getMessage());
            }
            
            
        }
    }
    
    private static class Order implements Runnable {
        private static String read() throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();
            return input;
        }
        private void order(String input) {
            if (input.equals("stop")) {
                MyLogger.Log("server stop");
                System.exit(0);
            } else if(input.equals("invite")) {
                InviteCode.Create();
            } else {
                System.err.println("Invalid Instruction");
            }
        }
        @Override
        public void run() {
            while (true) {
                try {
                    String input = read();
                    
                    order(input);
    
                } catch (IOException e) {
                    System.out.println(e);
                }
                
            }
        }
        
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }
        private static String readFile(String fileName) throws IOException {
        File file = new File(fileName);
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append('&');
        }
        bufferedReader.close();
        
        return stringBuilder.toString();
	    }

        @Override
        public void run() {
            try {
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                
                String readline = bufferedReader.readLine();
                String request = readline.substring(0, 4);
                
                String user = clientSocket.getInetAddress().getHostName();
                MyLogger.Log(request);
                if (request.equals("eula")) {
                    // 返回服务协议
                    String serverProtocolFile = "protocol.txt";
                    String serverProtocol = readFile(serverProtocolFile);
                    printWriter.println(serverProtocol);
                    MyLogger.Log(user + " Return Agreement");
                    String request1 = bufferedReader.readLine();
                } else if (request.equals("agre")) {
                    try {
                        String safe = readline.substring(4, 24);
                        if (InviteCode.check(safe)) {
                            String serverDataFile = "data.txt";
                            String serverData = readFile(serverDataFile);
                            printWriter.println(serverData);
                            MyLogger.Log(user + "Return Data");
                        } else {
                            printWriter.println("err");
                            MyLogger.Log(user + "Invitation code verification failed");
                        }
                        // 返回数据
                    } catch (Exception e) {
                        MyLogger.Log(user + "Invitation code verification failed");
                        System.err.println("Error handling client request: " + e.getMessage());
                    } 
                    
                    
                } else {
                    // 其他请求，返回无效消息
                    printWriter.println("无效请求，请重新发送。");
                    MyLogger.Log(user + " invalid request");
                }
                MyLogger.Log(user + "Turn off access");
                // 关闭连接
                bufferedReader.close();
                printWriter.close();
                clientSocket.close();

            } catch (IOException e) {
                System.err.println("Error handling client request: " + e.getMessage());
            }
        }
    }
    
    public static void nobug() {
        System.out.println("\u001B[33m███╗   ██╗ ██████╗ ██████╗ ██╗   ██╗ ██████╗ ");
        System.out.println("████╗  ██║██╔═══██╗██╔══██╗██║   ██║██╔════╝ ");
        System.out.println("██╔██╗ ██║██║   ██║██████╔╝██║   ██║██║  ███╗");
        System.out.println("██║╚██╗██║██║   ██║██╔══██╗██║   ██║██║   ██║");
        System.out.println("██║ ╚████║╚██████╔╝██████╔╝╚██████╔╝╚██████╔╝");
        System.out.println("╚═╝  ╚═══╝ ╚═════╝ ╚═════╝  ╚═════╝  ╚═════╝ \u001B[37m");
        System.out.println("===============[SRT-HarmonyLink]=============");
        System.out.println("        GenesisKeeper:L_fanhua v1.0.0");
    }


}

