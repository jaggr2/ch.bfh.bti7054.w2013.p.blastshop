package models;

import com.avaje.ebean.annotation.EnumValue;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 18.07.13
 * Time: 00:43
 */
@Entity
public class LocalFile extends BackendBasicModel {

    public enum SignatureAlgorithms {
        @EnumValue("rsa")
        RSA,

    }

    public enum HashAlgorithms {

        @EnumValue("MD5")
        MD5,

        @EnumValue("SHA-256")
        SHA256,

        @EnumValue("SHA-384")
        SHA384,

        @EnumValue("SHA-512")
        SHA512,

        @EnumValue("bcrypt")
        BCRYPT,
    }

    public String fileName;

    public Long fileSize;

    public String mimetype;

    @ManyToOne
    public Unit fileSizeUnit;

    public String fileHash;

    @Column(columnDefinition = "CHAR(8)")
    public HashAlgorithms fileHashAlgorithm;

    public SignatureAlgorithms fileSignatureAlgorithm;

    @Column(columnDefinition = "CHAR(36)")
    public String fileSignature;

    /**
     * Generiert den Hash-Wert des übergebenen String mit dem gewünschten Algorithmus
     *
     * @param algorithm
     *          Der Algorithmus, z.B. "SHA-256"
     *
     *          Zurzeit unterstütze Algorithmen
     *          - MD2
     *          - MD5
     *          - SHA-1
     *          - SHA-256
     *          - SHA-384
     *          - SHA-512
     * @param string_to_encrypt
     *            Der String, von dem der Hash-Wert generiert wird
     * @return Der Hash wird als hexadezimal-formatierter String zurückgegeben
     */
    private static String getHash(HashAlgorithms algorithm, String string_to_encrypt) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(algorithm.toString());
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
            return "";
        }
        digest.reset();
        byte[] data = digest.digest(string_to_encrypt.getBytes());

        // Formatiere Hexadezimal
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data)).toLowerCase();
    }

    public static byte[] getFileHash(HashAlgorithms algorithm, File targetFile) throws IOException, NoSuchAlgorithmException {
        InputStream fis =  new FileInputStream(targetFile);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance(algorithm.toString());
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        return complete.digest();
    }

    static String getFileHashHex(HashAlgorithms algorithm, File targetFile)
    {
        try {
            byte[] b = getFileHash(algorithm, targetFile);

            String result = "";

            for (int i=0; i < b.length; i++) {
                result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
            }
            return result;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void updateByFile(File targetFile) {
        this.fileSize = targetFile.length();
        this.fileHashAlgorithm = HashAlgorithms.MD5;
        this.fileHash = getFileHashHex(this.fileHashAlgorithm, targetFile);
    }


    public static Finder<Long, LocalFile> find = new Finder<Long, LocalFile>(Long.class, LocalFile.class);

    public static LocalFile create(String filename) {
        LocalFile newItem = new LocalFile();
        newItem.fileName = filename;
        newItem.rowStatus = RowStatus.TO_BE_VALIDATED;

        return newItem;
    }

    public static void initTestData() {
        // id 1
        LocalFile item1 = LocalFile.create("praline_croquant.jpg");
        item1.save();

        // id 2
        LocalFile item2 = LocalFile.create("pralinen-ei.jpg");
        item2.save();

        LocalFile item3 = LocalFile.create("apfelringe.jpg");
        item3.save();

        LocalFile item4 = LocalFile.create("gurke_zeugs.jpg");
        item4.save();

        LocalFile item5 = LocalFile.create("korb_gemischt.jpg");
        item5.save();

        LocalFile item6 = LocalFile.create("korb_redbull.jpg");
        item6.save();

        LocalFile item7 = LocalFile.create("marmelade_erdbeere.jpg");
        item7.save();

        LocalFile item8 = LocalFile.create("marmelade_johannisbeere.jpg");
        item8.save();

        LocalFile item9 = LocalFile.create("pflegeprodukt_photoshop.jpg");
        item9.save();

        LocalFile item10 = LocalFile.create("praline_cappucino.jpg");
        item10.save();

        LocalFile item11 = LocalFile.create("praline_coffee.jpg");
        item11.save();

        LocalFile item12 = LocalFile.create("praline_himbeere.jpg");
        item12.save();

        LocalFile item13 = LocalFile.create("praline_honig.jpg");
        item13.save();

        LocalFile item14 = LocalFile.create("praline_mandel.jpg");
        item14.save();

        LocalFile item15 = LocalFile.create("praline_sahne.jpg");
        item15.save();

        LocalFile item16 = LocalFile.create("praline_truffe.jpg");
        item16.save();

        LocalFile item17 = LocalFile.create("risotto_feinkoernig.jpg");
        item17.save();

        LocalFile item18 = LocalFile.create("risotto_grobkoernig.jpg");
        item18.save();

        LocalFile item19 = LocalFile.create("sirup_migros_siech.jpg");
        item19.save();

        LocalFile item20 = LocalFile.create("tee_schwarz_1.jpg");
        item20.save();

        LocalFile item21 = LocalFile.create("tee_schwarz_2.jpg");
        item21.save();
    }

}
