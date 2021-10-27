package com.example.aadharaddressupdation.ui.otpverification;

import androidx.annotation.NonNull;

import com.example.aadharaddressupdation.models.Opts;
import com.example.aadharaddressupdation.models.Otp;
import com.example.aadharaddressupdation.models.Type;
import com.example.aadharaddressupdation.models.signing.SignedXml;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.URI;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.Properties;
import java.util.TimeZone;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

public class OtpAPIService {

    Properties configProp = new Properties();

    void readProperties() throws IOException {
        InputStream in = Objects.requireNonNull(this.getClass().getClassLoader()).getResourceAsStream("application.properties");
        configProp.load(in);
    }

//    OtpRes getOtpRes(@NonNull String uid, String txnId) throws Exception {
//        Type type;
//        if(uid.length()>12){
//            type = Type.V;
//        }else {
//            type = Type.A;
//        }
//        Otp otp = createOtpRequest(configProp.getProperty(Constants.AUTH_REQUEST_AUA),
//                configProp.getProperty(Constants.AUTH_REQUEST_ASA),configProp.getProperty(Constants.AUTH_REQUEST_AUA_LK),
//                uid, txnId, type);
//
//        return getParsedResponseFromOtpServer(otp);
//
//    }

    @NonNull
    private Otp createOtpRequest(String aua, String sa, String licenseKey,
                                 String uid, String txn, Type type) {


        Otp otpReq = new Otp();
        otpReq.setUid(uid);

        otpReq.setVer("2.5");
        otpReq.setAc(aua);
        otpReq.setSa(sa);
        otpReq.setType(type);

        otpReq.setTxn(txn);

        otpReq.setLk(licenseKey);
        // Using India TZ
        Calendar calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
//        otpReq.setTs(XMLGregorianCalendarImpl.createDateTime(
//                calendar.get(Calendar.YEAR),
//                calendar.get(Calendar.MONTH) + 1,
//                calendar.get(Calendar.DAY_OF_MONTH),
//                calendar.get(Calendar.HOUR_OF_DAY),
//                calendar.get(Calendar.MINUTE),
//                calendar.get(Calendar.SECOND)));

        Opts c = new Opts();
        c.setCh("01");
        otpReq.setOpts(c);

        return otpReq;
    }



    @NonNull
//    private String generateSignedOtpXML(Otp otp) throws Exception {
//        StringWriter otpXML = new StringWriter();
//
//        JAXBElement<Otp> element = new JAXBElement<>(new QName(
//                "http://www.uidai.gov.in/authentication/otp/1.0", "Otp"),
//                Otp.class, otp);
//
//        JAXBContext.newInstance(Otp.class).createMarshaller()
//                .marshal(element, otpXML);
//
//        return signXML(otpXML.toString());
//    }

//    private OtpRes parseOtpResponseXML(String xmlToParse) throws JAXBException, SAXException {
//
//        JAXBContext jc = JAXBContext.newInstance(OtpRes.class);
//        Unmarshaller u = jc.createUnmarshaller();
//
//        XMLReader reader;
//        reader = XMLReaderFactory.createXMLReader();
//
//        // Create the filter (to add namespace) and set the xmlReader as its
//        // parent.
//        NamespaceFilter inFilter = new NamespaceFilter(
//                "http://www.uidai.gov.in/authentication/otp/1.0", true);
//        inFilter.setParent(reader);
//
//        // Prepare the input, in this case a java.io.File (output)
//        InputSource is = new InputSource(new StringReader(xmlToParse));
//
//        // Create a SAXSource specifying the filter
//        SAXSource source = new SAXSource(inFilter, is);
//
//        // Do unmarshalling
//        return u.unmarshal(source, OtpRes.class).getValue();
//    }

    public String getParsedResponseFromOtpServer(String xml, String uid) throws Exception {
        //String signedXML = generateSignedOtpXML(otp);



        //This was code for XmlSigner.java, we might have to use it
        InputStream jks = XmlSigner.class.getResourceAsStream(configProp.getProperty(Constants.SIGNATURE_FILE));
        SignedXml signedXml=new XmlSigner()
                .withXml(xml)
                .withKeyStore(jks, configProp.getProperty(Constants.SIGNATURE_ALIAS), configProp.getProperty(Constants.SIGNATURE_PASSWORD))
                .sign();
        String signedXML=signedXml.getContent();
        //        if (otp.getUid().length() < 16) {
//            uriString = configProp.getProperty(Constants.URL)
//                    + (configProp.getProperty(Constants.URL).endsWith("/") ? "" : "/")
//                    + otp.getAc() + "/" + otp.getUid().charAt(0) + "/"
//                    + otp.getUid().charAt(1);
//        } else {
//            uriString = configProp.getProperty(Constants.URL)
//                    + (configProp.getProperty(Constants.URL).endsWith("/") ? "" : "/")
//                    + otp.getAc() + "/" + "0" + "/"
//                    + "0";
//        }

        String uriString = configProp.getProperty(Constants.URL)
                + (configProp.getProperty(Constants.URL).endsWith("/") ? "" : "/")
                + (configProp.getProperty(Constants.AUTH_REQUEST_AUA)) + "/" + uid.charAt(0) + "/"
                + uid.charAt(1);

        uriString = uriString + "/" + configProp.getProperty(Constants.AUTH_REQUEST_ASA_LK);

        URI otpURI = new URI(uriString);

        WebResource webResource = Client.create().resource(otpURI);

        return webResource.header("REMOTE_ADDR",
                InetAddress.getLocalHost().getHostAddress()).post(
                String.class, signedXML);

    }

    @NonNull
    private String signXML(String xmlDocument) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        // Parse the input XML
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document inputDocument = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xmlDocument)));

        // Sign the input XML's DOM document
        Document signedDocument = sign(inputDocument);

        // Convert the signedDocument to XML String
        StringWriter stringWriter = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        trans.transform(new DOMSource(signedDocument), new StreamResult(stringWriter));

        return stringWriter.getBuffer().toString();
    }

    private Document sign(@NonNull Document xmlDoc) throws Exception {

        // Creating the XMLSignature factory.
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
        // Creating the reference object, reading the whole document for
        // signing.
        Reference ref = fac.newReference("", fac.newDigestMethod(DigestMethod.SHA1, null),
                Collections.singletonList(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)), null,
                null);

        // Create the SignedInfo.
        SignedInfo sInfo = fac.newSignedInfo(
                fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
                fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null), Collections.singletonList(ref));

        X509Certificate x509Cert = (X509Certificate) getAuthReqKeyFromKeyStore().getCertificate();

        KeyInfo kInfo = getKeyInfo(x509Cert, fac);
        DOMSignContext dsc = new DOMSignContext(getAuthReqKeyFromKeyStore().getPrivateKey(), xmlDoc.getDocumentElement());
        XMLSignature signature = fac.newXMLSignature(sInfo, kInfo);
        signature.sign(dsc);

        Node node = dsc.getParent();
        return node.getOwnerDocument();

    }

    private KeyStore.PrivateKeyEntry getAuthReqKeyFromKeyStore() throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream(configProp.getProperty(Constants.SIGNATURE_FILE))){
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(fileInputStream, configProp.getProperty(Constants.SIGNATURE_PASSWORD).toCharArray());
            return (KeyStore.PrivateKeyEntry) keyStore.getEntry(configProp.getProperty(Constants.SIGNATURE_ALIAS),
                    new KeyStore.PasswordProtection(configProp.getProperty(Constants.SIGNATURE_PASSWORD).toCharArray()));
        }
    }

    private KeyInfo getKeyInfo(X509Certificate cert, @NonNull XMLSignatureFactory fac) {
        // Create the KeyInfo containing the X509Data.
        KeyInfoFactory kif = fac.getKeyInfoFactory();
        X509Data xd = kif.newX509Data(Arrays.asList(cert.getSubjectX500Principal().getName(), cert));
        return kif.newKeyInfo(Collections.singletonList(xd));
    }

}