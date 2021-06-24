package ru.kpfu.elina.utils;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class XmlGenerator {



    private String xmlFilePath;
//    private String[] lines;
    private ArrayList<String> lines;
    private File messageFile;
    private String dirPath = "C:\\Users\\elina\\Desktop\\dev-tools-master\\dev-tools-master\\src\\main\\java\\ru\\kpfu\\elina\\messages";

    public XmlGenerator() {
    }

    public ArrayList<String> generateXml(int numberFile){

            try {
                messageFile = new File("C:\\Users\\elina\\Desktop\\dev-tools-master\\dev-tools-master\\src\\main\\java\\ru\\kpfu\\elina\\messages" + "/" + numberFile + ".xml");

                DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

                DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

                Document document = documentBuilder.newDocument();

                //Ed701
                Element root = document.createElement("ED701");
                document.appendChild(root);

                //AccDoc
                Element accDoc = document.createElement("AccDoc");
                root.appendChild(accDoc);

                Attr accDocNo = document.createAttribute("AccDocNo");
                accDocNo.setValue("string");
                accDoc.setAttributeNodeNS(accDocNo);

                Attr accDocDate = document.createAttribute("AccDocDate");
                accDocDate.setValue("2008-11-15");
                accDoc.setAttributeNodeNS(accDocDate);

                //PAYER
                Element payer = document.createElement("Payer");
                root.appendChild(payer);

                Element organisationInfo = document.createElement("OrganisationInfo");
                payer.appendChild(organisationInfo);

                Element organisationName = document.createElement("OrganisationName");
                organisationName.appendChild(document.createTextNode("Organization name"));
                organisationInfo.appendChild(organisationName);

                Element organisationIdentification = document.createElement("OrganisationIdentification");
                organisationInfo.appendChild(organisationIdentification);

                Attr organisationIdentificatorType = document.createAttribute("OrganisationIdentificatorType");
                organisationIdentificatorType.setValue("TXID");
                organisationIdentification.setAttributeNodeNS(organisationIdentificatorType);

                Attr identificatorValue = document.createAttribute("IdentificatorValue");
                identificatorValue.setValue("string");
                organisationIdentification.setAttributeNodeNS(identificatorValue);

                Element accountInfo = document.createElement("AccountInfo");
                payer.appendChild(accountInfo);

                Attr accountType = document.createAttribute("AccountType");
                accountType.setValue("BBAN");
                accountInfo.setAttributeNodeNS(accountType);

                Attr accountValue = document.createAttribute("AccountValue");
                accountValue.setValue("string");
                accountInfo.setAttributeNodeNS(accountValue);

                Element address = document.createElement("Address");
                address.appendChild(document.createTextNode("       Карбышева 1"));
                payer.appendChild(address);

                Element bank = document.createElement("Bank");
                payer.appendChild(bank);

                Attr bic = document.createAttribute("BIC");
                bic.setValue("string");
                bank.setAttributeNodeNS(bic);

                Attr correspAcc = document.createAttribute("CorrespAcc");
                correspAcc.setValue("string");
                bank.setAttributeNodeNS(correspAcc);


                //PAYEE
                Element payee = document.createElement("Payee");
                root.appendChild(payee);

                Element personInfoPayee = document.createElement("PersonInfo");
                payee.appendChild(personInfoPayee);

                Element personNamePayee = document.createElement("PersonName");
                personNamePayee.appendChild(document.createTextNode("Хасанова Эльвира Фависовна"));
                personInfoPayee.appendChild(personNamePayee);

                Element accountInfoPayee = document.createElement("AccountInfo");
                payee.appendChild(accountInfoPayee);

                Attr accountTypePayee = document.createAttribute("AccountType");
                accountTypePayee.setValue("EPID");
                accountInfoPayee.setAttributeNodeNS(accountTypePayee);

                Attr accountValuePayee = document.createAttribute("AccountValue");
                accountValuePayee.setValue("string");
                accountInfoPayee.setAttributeNodeNS(accountValuePayee);

                Element bankPayee = document.createElement("Bank");
                payee.appendChild(bankPayee);

                Attr bicPayee = document.createAttribute("BIC");
                bicPayee.setValue("string");
                bankPayee.setAttributeNodeNS(bicPayee);

                Attr correspAccPayee = document.createAttribute("CorrespAcc");
                correspAccPayee.setValue("string");
                bankPayee.setAttributeNodeNS(correspAccPayee);

                //PURPOSE
                Element purpose = document.createElement("Purpose");
                purpose.appendChild(document.createTextNode("Возвращаю долг"));
                root.appendChild(purpose);


                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource domSource = new DOMSource(document);
                StreamResult streamResult = new StreamResult(messageFile);

                transformer.transform(domSource, streamResult);

                System.out.println("Done creating XML File");

                lines = new ArrayList<>();

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(messageFile), Charset.forName("UTF-8")))) {

                    String line;
                    int i = 0;

//                    while ((line = reader.readLine()) != null && i < 100) {
                    while ((line = reader.readLine()) != null) {
//                        lines[i++] = line;
                        lines.add(line);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            } catch (ParserConfigurationException pce) {
                pce.printStackTrace();
            } catch (TransformerException tfe) {
                tfe.printStackTrace();
            }

            return lines;

        }




    public ArrayList<String> getLines() {
        return lines;
    }




}

