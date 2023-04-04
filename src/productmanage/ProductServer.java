package productmanage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONObject;


public class ProductServer {
    ServerSocket serverSocket;
    int num = 0;
    ExecutorService threadPool;
    List<Product> productList = new ArrayList<>();



    public void start() throws IOException {
        serverSocket = new ServerSocket(50001);
        threadPool = Executors.newFixedThreadPool(100);

        System.out.println("[서버] 시작됨");
        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    Socket socket = serverSocket.accept();
                    new SocketClient(this, socket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }


    //메소드: 서버 종료
    public void stop() {
        try {
            serverSocket.close();
            threadPool.shutdownNow();
            System.out.println("[서버] 종료됨 ");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            ProductServer productServer = new ProductServer();
            productServer.start();

            System.out.println("----------------------------------------------------");
            System.out.println("서버를 종료하려면 q를 입력하고 Enter.");
            System.out.println("----------------------------------------------------");

            Scanner scanner = new Scanner(System.in);

            while (true) {
                String key = scanner.nextLine();
                if (key.equals("q")) break;
            }

            scanner.close();
            productServer.stop();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[서버] " + e.getMessage());
        }
    }


    public class SocketClient {
        //필드
        ProductServer productServer;
        Socket socket;
        DataInputStream dis;
        DataOutputStream dos;
        String managerIp;

        //생성자
        public SocketClient(ProductServer productServer, Socket socket) {
            try {
                this.productServer = productServer;
                this.socket = socket;
                this.dis = new DataInputStream(socket.getInputStream());
                this.dos = new DataOutputStream(socket.getOutputStream());
                InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
                //socket의 주소를 InsetSocketAddress 클래스로 캐스팅
                this.managerIp = isa.getHostName();
                receive();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //메소드: JSON 받기
        public void receive() {
            productServer.threadPool.execute(() -> {
                try {
                    while (true) {

                        JSONObject responseJsonObject = new JSONObject();

                        // READ
                        JSONArray jsonArray = new JSONArray();
                        for(Product productRead : productList) { //iterable

                            JSONObject jsonReadObject = new JSONObject();
                            System.out.println("he");
                            jsonReadObject.put("no",productRead.getNo());
                            jsonReadObject.put("name",productRead.getName());
                            jsonReadObject.put("price",productRead.getPrice());
                            jsonReadObject.put("stock",productRead.getStock());

                            jsonArray.put(jsonReadObject);
                        }

                        responseJsonObject.put("status", "success");
                        responseJsonObject.put("data", jsonArray);

                        String json = responseJsonObject.toString();
                        send(json);
                        System.out.println("status 성공여부 확인: "+json);

                        String receiveJson = dis.readUTF(); //입력받을 때까지 블록
                        System.out.println("socket receive()확인 "+receiveJson);
                        JSONObject jsonObject = new JSONObject(receiveJson);

                        String menu = jsonObject.getString("menu");
                        JSONObject jsonDataObject = jsonObject.getJSONObject("data");

                        Product product = new Product();

                        try {
                            switch (menu) {
                                case "1": // create
                                    product.setNo(++num);
                                    product.setName(jsonDataObject.getString("name"));
                                    product.setPrice(jsonDataObject.getInt("price"));
                                    product.setStock(jsonDataObject.getInt("stock"));

                                    productList.add(product);
                                    break;

                                case "2": // update
                                    int no2 = jsonDataObject.getInt("no");

                                    for(int i = 0; i < productList.size(); i++){
                                        if(productList.get(i).getNo() == no2) {
                                            productList.get(i).setName(jsonDataObject.getString("name"));
                                            productList.get(i).setPrice(jsonDataObject.getInt("price"));
                                            productList.get(i).setStock(jsonDataObject.getInt("stock"));
                                            break;
                                        }
                                    }
                                    break;

                                case "3":
                                    int no3 = jsonDataObject.getInt("no");

                                    for(int i = 0; i<productList.size(); i++) {
                                        if (productList.get(i).getNo() == no3) {
                                            productList.remove(i);
                                        }
                                    }
                                    break;
                                default:
                                    this.close();
                                    break;
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                            responseJsonObject.put("status", "fail");

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    this.close();
                }
            });
        }

        public void send(String json) {
            try {
                dos.writeUTF(json);
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void close() {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}