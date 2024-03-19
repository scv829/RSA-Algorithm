import javax.crypto.Cipher;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class pem {

    private static final String KEY_PATH = Server.serverPath;

    //키파일 있는지 확인
    public static boolean checkPem(){
        return (new File(KEY_PATH + "publicKey.pem")).exists();
    }
    //공개키,비밀키 생성
    public static KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // 키 크기 설정
        return keyPairGenerator.generateKeyPair();
    }

    //공개키,비밀키 저장
    public static void saveKeyToFile(String fileName, Key key) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            byte[] keyBytes = key.getEncoded();
            String keyPEM = new String(Base64.getEncoder().encode(keyBytes));
            fos.write(keyPEM.getBytes());
        }
    }

    //공개키 읽기
    public static PublicKey readPublicKeyFromFile(String fileName) throws Exception {
        String keyPEM = new String(Files.readAllBytes(Paths.get(fileName)));
        keyPEM = keyPEM.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
        byte[] keyBytes = Base64.getDecoder().decode(keyPEM);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    //비밀키 읽기
    public static PrivateKey readPrivateKeyFromFile(String fileName) throws Exception {
        String keyPEM = new String(Files.readAllBytes(Paths.get(fileName)));
        keyPEM = keyPEM.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");
        byte[] keyBytes = Base64.getDecoder().decode(keyPEM);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    // 암호화: 공개키로 진행
    public static String encrypt(String plainText, PublicKey publicKey) {
        String encryptedText = null;

        try {
            // 만들어진 공개키 객체로 암호화 설정
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encryptedText;
    }

    // 복호화: 개인키로 진행
    public static String decrypt(String encryptedText, PrivateKey privateKey) {
            String decryptedText = null;

            try {
                // 만들어진 공개키 객체로 복호화 설정
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, privateKey);

                // 암호문을 평문화하는 과정
                byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText.getBytes());
                byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
                decryptedText = new String(decryptedBytes);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return decryptedText;
    }

}
