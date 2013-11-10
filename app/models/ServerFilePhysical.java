package models;

import com.avaje.ebean.annotation.EnumValue;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 18.07.13
 * Time: 00:43
 */
@Entity
public class ServerFilePhysical extends BackendBasicModel {

    public enum SignatureAlgorithms {
        @EnumValue("rsa")
        RSA,

    }

    public enum HashAlgorithms {

        @EnumValue("MD5")
        MD5,

        @EnumValue("SHA-256")
        SHA256,

        @EnumValue("bcrypt")
        BCRYPT,
    }

    @ManyToOne
    public ServerFile serverFile;

    public Integer revision;

    public String physicalFilename;

    public Long fileSize;

    @ManyToOne
    public Unit fileSizeUnit;

    public String fileHash;

    @Column(columnDefinition = "CHAR(8)")
    public HashAlgorithms fileHashAlgorithm;

    public SignatureAlgorithms fileSignatureAlgorithm;

    @Column(columnDefinition = "varchar(700)")
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
}
