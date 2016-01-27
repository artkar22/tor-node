package diffieHellman;

import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Diffie-Hellman module
 */
public class DiffieHellmanModule {
    private static final int AES_KEY_SIZE = 128;
    private static KeyPairGenerator kpg;

    static {
        try {
            // === Generates and inits a KeyPairGenerator ===

            // changed this to use default parameters, generating your
            // own takes a lot of time and should be avoided
            // use ECDH or a newer Java (8) to support key generation with
            // higher strength
            kpg = KeyPairGenerator.getInstance("DH");
            kpg.initialize(1024);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create KeyAgreement and generate secret key
     * 
     * @param prk_self
     *            the private key from the user who wants to generate the secret
     *            key
     * @param pbk_peer
     *            the public key from the user whom is to be agree on the secret
     *            key with
     * @param lastPhase
     *            flag which indicates whether or not this is the last phase of
     *            this key agreement
     * @return the secret key
     */
    public static SecretKey agreeSecretKey(PrivateKey prk_self,
            PublicKey pbk_peer, boolean lastPhase) throws Exception {
        // instantiates and inits a KeyAgreement
        KeyAgreement ka = KeyAgreement.getInstance("DH");
        ka.init(prk_self);
        // Computes the KeyAgreement
        ka.doPhase(pbk_peer, lastPhase);
        // Generates the shared secret
        byte[] secret = ka.generateSecret();

        // === Generates an AES key ===

        // you should really use a Key Derivation Function instead, but this is
        // rather safe

        MessageDigest sha256 = MessageDigest.getInstance("SHA-256"); 
        byte[] bkey = Arrays.copyOf(
                sha256.digest(secret), AES_KEY_SIZE / Byte.SIZE);

        SecretKey desSpec = new SecretKeySpec(bkey, "AES");
        return desSpec;
    }


    /**
     * Generate a key pair of algorithm "DiffieHellman"
     * 
     * @return the public and private key pair
     */
    public static KeyPair genDHKeyPair() {
        return kpg.genKeyPair();
    }
}
