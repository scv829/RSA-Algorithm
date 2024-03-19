import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;


public class Server {
    static String serverPath = "C:/Users/han/Desktop/server/";

    public static void main(String[] args) throws Exception {
        try {
            ServerSocket server = new ServerSocket(15000);
            Socket sock = null;

            //키 존재 여부 확인
            if(!pem.checkPem()) {
                KeyPair keyPair = pem.generateRSAKeyPair();

                // 공개키와 비밀키를 각각 파일로 저장
                pem.saveKeyToFile(serverPath + "publicKey.pem", keyPair.getPublic());
                pem.saveKeyToFile(serverPath + "privateKey.pem", keyPair.getPrivate());
                System.out.println("RSA 키 생성 및 파일 저장이 완료되었습니다.");
            }

            while(true) {
                System.out.println("클라이언트 접속 대기 중");
                sock = server.accept();
                System.out.println(sock.getInetAddress()+ " 접속");
                try {
                    while(true) {
                        DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
                        DataInputStream dis = new DataInputStream(sock.getInputStream());

                        //유형 받기
                        int type = dis.readInt();
                        //파일 받기
                        String fileName = dis.readUTF();            //파일 이름
                        int fileSize = dis.readInt();               //파일 크기
                        byte[] fileContents = new byte[fileSize];   //파일 내용 받을 배열
                        dis.readFully(fileContents);                //파일 내용
                        String clientPath = dis.readUTF();          //보낸 파일의 위치

                        if(type == 1){
                            // 공개키 파일 읽기
                            PublicKey publicKey = pem.readPublicKeyFromFile(serverPath + "publicKey.pem");

                            String plainText = new String(fileContents);
                            System.out.println("평문: " + plainText);

                            String encryptedText = pem.encrypt(plainText, publicKey);
                            System.out.println("암호화: " + encryptedText);

                            saveToFile(clientPath+fileName,encryptedText);
                        }
                        else if(type == 2){
                            // 개인키 파일 읽기
                            PrivateKey privateKey = pem.readPrivateKeyFromFile(serverPath + "privateKey.pem");

                            String plainText = new String(fileContents);
                            System.out.println("암호문: " + plainText);

                            String decryptedText = pem.decrypt(plainText, privateKey);
                            System.out.println("해독문: " + decryptedText);

                            saveToFile(clientPath+fileName,decryptedText);
                        }
                    }
                } catch(Exception e){
                    continue;
                }
            }
        } catch (Exception e) {
            System.out.println("연결 오류");
        }
    }

    //파일 저장
    private static void saveToFile(String fileName, String contents) {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(contents);
            System.out.println("내용 저장");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}