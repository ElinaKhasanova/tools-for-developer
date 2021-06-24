package ru.kpfu.elina.utils;

import org.apache.xml.security.c14n.Canonicalizer;
import org.w3c.dom.Document;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

public class CipherXml {
//    private File cipherFile;
//    private String dirCipherMessage = "C:\\Users\\elina\\Desktop\\dev-tools-master\\dev-tools-master\\src\\main\\java\\com\\github\\reugn\\devtools\\ciphers";
//    private String dirXmlMessage = "C:\\Users\\elina\\Desktop\\dev-tools-master\\dev-tools-master\\src\\main\\java\\com\\github\\reugn\\devtools\\messages\\0.xml";

    public CipherXml() {
    }

    public void processXml(String xmlMessageForProcess, String txtFileForWrite){
        normalizeXmlMessage(xmlMessageForProcess);
    }

    private void normalizeXmlMessage(String filename){
        try {
            File inputFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

//    private void canonXmlMessage(String filename){
//        try {
//            documentToByte(xml);
//            Canonicalizer canon = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
//            byte canonXmlBytes[] = canon.canonicalize(filename);
//            String canonXmlString = new String(canonXmlBytes);
//
//        } catch (Exception ex){
//            ex.printStackTrace();
//        }
//    }

    public byte[] documentToByte(Document document) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        org.apache.xml.security.utils.XMLUtils.outputDOM(document, baos, true);
        return baos.toByteArray();
    }



    public void cipherXml(String xmlMessageForCipher, String txtFileForWrite){

        try {
        String xmlString = readUsingBufferedReader(xmlMessageForCipher);
        System.out.println("Строка, записанная из файла - " + xmlString);

        KeyPairGenerator generator = KeyPairGenerator.getInstance("DH");

        KeyPair aliceKeyPair = generator.generateKeyPair();
        byte[] alicePubKeyEncoded = aliceKeyPair.getPublic().getEncoded();

        KeyFactory bobKeyFactory = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(alicePubKeyEncoded);
        PublicKey alicePubKey = bobKeyFactory.generatePublic(x509KeySpec);

        DHParameterSpec dhParamFromAlicePubKey = ((DHPublicKey) alicePubKey).getParams();

        KeyPairGenerator bobKpairGen = KeyPairGenerator.getInstance("DH");
        bobKpairGen.initialize(dhParamFromAlicePubKey);
        KeyPair bobKeyPair = bobKpairGen.generateKeyPair();
        byte[] bobPubKeyEncoded = bobKeyPair.getPublic().getEncoded();

        KeyAgreement aliceKeyAgree = KeyAgreement.getInstance("DH");
        aliceKeyAgree.init(aliceKeyPair.getPrivate());
        KeyFactory aliceKeyFactory = KeyFactory.getInstance("DH");
        x509KeySpec = new X509EncodedKeySpec(bobPubKeyEncoded);
        PublicKey bobPubKey = aliceKeyFactory.generatePublic(x509KeySpec);
        aliceKeyAgree.doPhase(bobPubKey, true);
        byte[] aliceSharedSecret = aliceKeyAgree.generateSecret();
        SecretKeySpec aliceAesKey = new SecretKeySpec(aliceSharedSecret, 0, 16, "AES");

        KeyAgreement bobKeyAgree = KeyAgreement.getInstance("DH");
        bobKeyAgree.init(bobKeyPair.getPrivate());
        bobKeyAgree.doPhase(alicePubKey, true);
        byte[] bobSharedSecret = bobKeyAgree.generateSecret();
        SecretKeySpec bobAesKey = new SecretKeySpec(bobSharedSecret, 0, 16, "AES");

        System.out.println("Shared keys are equals: " + Arrays.equals(aliceSharedSecret, bobSharedSecret));

        Cipher bobCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        bobCipher.init(Cipher.ENCRYPT_MODE, bobAesKey);
        byte[] ciphertext = bobCipher.doFinal(xmlString.getBytes());
        System.out.println("Зашифрованный текст - " + ciphertext);

        //BASE-64
        Base64.Encoder enc = Base64.getEncoder();
        String encoded = enc.encodeToString(ciphertext.toString().getBytes());
        System.out.println("Переведенное в base64 \t" + encoded);

        writeUsingBudderedReader(encoded, txtFileForWrite);

        byte[] encodedParamsFromBob = bobCipher.getParameters().getEncoded();

        AlgorithmParameters aesParams = AlgorithmParameters.getInstance("AES");
        aesParams.init(encodedParamsFromBob);
        Cipher aliceCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        aliceCipher.init(Cipher.DECRYPT_MODE, aliceAesKey, aesParams);
        byte[] recovered = aliceCipher.doFinal(ciphertext);
        System.out.println("Расшифрованное сообщение - " + new String(recovered));
    } catch (Exception exception){
        exception.printStackTrace();
    }
    }

    private static String readUsingBufferedReader(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        while( ( line = reader.readLine() ) != null ) {
            stringBuilder.append( line );
            stringBuilder.append( ls );
        }

        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        return stringBuilder.toString();
    }

    private static void writeUsingBudderedReader (String cipher, String filename) throws IOException{
        FileOutputStream fos = new FileOutputStream(filename);
        byte[] buffer = cipher.getBytes();
        fos.write(buffer, 0, buffer.length);
        System.out.println("записано в файл " + filename + " : " + cipher);
    }

}

