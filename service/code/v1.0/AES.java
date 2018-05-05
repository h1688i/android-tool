package com.android.device;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
	public final static String IvAES = "密碼" ;
	public final static String KeyAES = "金鑰";
	public static byte[] EncryptAES(byte[] iv, byte[] key, byte[] text) {
		try {
			AlgorithmParameterSpec mAlgorithmParameterSpec = new IvParameterSpec(iv);
			SecretKeySpec mSecretKeySpec = new SecretKeySpec(key, "AES");
			Cipher mCipher = null;
			mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			mCipher.init(Cipher.ENCRYPT_MODE, mSecretKeySpec, mAlgorithmParameterSpec);

			return mCipher.doFinal(text);
		} catch (Exception ex) {
			return null;
		}
	}

	// AES解密，帶入byte[]型態的16位英數組合文字、32位英數組合Key、需解密文字
	public static byte[] DecryptAES(byte[] iv, byte[] key, byte[] text) {
		try {
			AlgorithmParameterSpec mAlgorithmParameterSpec = new IvParameterSpec(iv);
			SecretKeySpec mSecretKeySpec = new SecretKeySpec(key, "AES");
			Cipher mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			mCipher.init(Cipher.DECRYPT_MODE, mSecretKeySpec, mAlgorithmParameterSpec);

			return mCipher.doFinal(text);
		} catch (Exception ex) {
			return null;
		}
	}
}
