package rsa;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

public class Test{
    /*
    公钥＝(e , n)
    私钥＝(d , n)
    质数 p q
    公共模数 n=p*q
    欧拉函数 r=(p-1)*(q-1)
    公钥 e 1<e<φ(n) e为整数，且e和φ(n)是互质数
    私钥 d  e*d % φ(n) = 1
    密文 C
    明文 M
    加密后密文 c ＝ M^e mod n
    解密后明文 m ＝ C^D mod n
    */

    private static int p=61;
    private static int q=53;
    private static int n;
    private static int r;
    private static int e;
    private static int d;
    private static int x=0;
    private static int y=0;
    private static String M;
    private static String C;
    private static String c;
    private static String m;


    //指定加密算法为RSA
    private static String ALGORITHM = "RSA";
    //指定key的大小
    private static int KEYSIZE = 1024;
    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    public static void main(String[] args) throws Exception {
        if(isPrime(p)&&isPrime(q)){

            // 求欧拉函数r 和 公共模数n
            r=(p-1)*(q-1);
            n=p*q;

            //公钥e
            Random ran=new Random();
            //rand.nextInt(MAX-MIN+1)+MIN; 将被赋值为一个MIN和MAX范围内的随机数
            e=ran.nextInt(r-2)+2;//e的范围是[2，r-1]
            while(!isCoprime(r,e)){//判断e和φ(n)是否互为质数
                e=ran.nextInt(r-2)+2;
            }
            System.out.println(r);
            System.out.println(e);

            //私钥d
            d=myEuclid(e,r);
            //c=encode(M,e,n);
            //m=decode(C,e,n);
            //System.out.println("q:"+q+" "+"p:"+p+" "+"公共模数n:"+n+" "+"欧拉函数r:"+r+" "+"公钥e:"+ e +" "+"私钥d:"+d+" "+"加密:"+c+" "+"解密:"+m);

            Key[] keypair=generateKeyPair();
            String publickey=encryptBASE64(keypair[0].getEncoded());
            String privatekey=encryptBASE64(keypair[1].getEncoded());
            M="Hello world!";//待加密的明文
            m=encrypt(M,keypair[0]);// 生成的密文
            c= decrypt(m,keypair[1]);// 解密密文
            System.out.println("公钥:"+ publickey);
            System.out.println("私钥:"+ privatekey);
            System.out.println("密文:"+ m);
            System.out.println("明文:"+ c);


            byte[]bytes =M.getBytes();
            // 产生签名
            String sign = sign(bytes,privatekey);
            System.err.println("签名:" +sign);

            // 验证签名
            boolean status = verify(bytes, publickey,sign);
            System.err.println("状态:" +status);




        }else{
            System.out.println("输入非质数，请检查");
        }
    }
    //素数判断
    public static boolean isPrime(int num){
        if (num == 1)
            return false;
        int max = (int) Math.sqrt(num);
        for (int i = 2; i <= max; i++)
            if (num % i == 0)
                return false;
        return true;
    }
    //互质判断
    public static boolean isCoprime(int num1,int num2){
        //求最大公约数
        int i=num1%num2;
        while(i!=0){
            num1=num2;
            num2=i;
            i=num1%num2;
        }
        if(num2==1) return true;
        else return false;
    }
    //求私钥d(即求逆元)
    public  static int myEuclid(int e, int modValue){
        int num = e;
        int d = 1;
        while((num % modValue ) != 1){
            d++;
            num += e;
        }
        //System.out.println(d);
        return d;
    }
    /*//加密 C ＝ M^e mod n
    public static String encode(String value, int e, int n){
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
//        String[] chars = value.split(" ");
        for (int i = 0; i < chars.length; i++) {
            if(i != chars.length - 1)
            {
                sbu.append(Math.floorMod((long)Math.pow(chars[i], e), n)).append(" ");
            }
            else {
                sbu.append(Math.floorMod((long)Math.pow(chars[i], e), n));
            }
        }
        return sbu.toString();
    }
    //    解密 M ＝ C^D mod n
    public static String decode(String value, int d, int n){
        StringBuffer sbu1 = new StringBuffer();
        char[] chars = value.toCharArray();
//        String[] chars = value.split(" ");
        for (int i = 0; i < chars.length; i++) {
            if(i != chars.length - 1)
            {
                sbu1.append(Math.floorMod((long)Math.pow(chars[i], d), n)).append(" ");
            }
            else {
                sbu1.append(Math.floorMod((long)Math.pow(chars[i], d), n));
            }
        }
        return sbu1.toString();
    }
*/
    //生成密钥对
    public static Key[] generateKeyPair() throws Exception {
        // RSA算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        //为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGORITHM);
        //利用上面的随机数据源初始化这个KeyPairGenerator对象
        kpg.initialize(KEYSIZE, sr);
        //生成密匙对
        KeyPair kp = kpg.generateKeyPair();
        Key[] keypair=new Key[2];
        //得到公钥
        keypair[0] = kp.getPublic();
        //得到私钥
        keypair[1] = kp.getPrivate();
        //返回公私钥
        return keypair;
    }

    //加密算法
    public static String encrypt(String M,Key publickey) throws Exception {
        //得到Cipher对象来实现对源数据的RSA加密
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publickey);
        int MaxBlockSize = KEYSIZE / 8 ;
        //System.out.println("公钥长度："+KEYSIZE+"\n");
        String[] datas = splitString(M, MaxBlockSize - 11);
        String m = "";
        for (String s : datas) {
            m += bcd2Str(cipher.doFinal(s.getBytes()));
        }
        return m;
    }

    private static String bcd2Str(byte[] bytes) {
        char temp[] = new char[bytes.length * 2], val;


        for (int i = 0; i < bytes.length; i++) {
            val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
            temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');


            val = (char) (bytes[i] & 0x0f);
            temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
        }
        return new String(temp);
    }

    private static String[] splitString(String string, int len) {
        int x = string.length() / len;
        int y = string.length() % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        String[] strings = new String[x + z];
        String str = "";
        for (int k=0; k<x+z; k++) {
            if (k==x+z-1 && y!=0) {
                str = string.substring(k*len, k*len+y);
            }else{
                str = string.substring(k*len, k*len+len);
            }
            strings[k] = str;
        }
        return strings;
    }

    //解密算法
    public static String decrypt(String C,Key privatekey) throws Exception {
        //得到Cipher对象对已用公钥加密的数据进行RSA解密
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privatekey);
        int key_len = KEYSIZE / 8 ;
        //System.out.println("私钥长度："+KEYSIZE+"\n");
        byte[] bytes = C.getBytes();
        byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
        //System.err.println(bcd.length);
        String c = "";
        byte[][] arrays = splitArray(bcd, key_len);
        for(byte[] arr : arrays){
            c += new String(cipher.doFinal(arr));
        }
        return c;
    }

    private static byte[][] splitArray(byte[] data,int len) {
        int x = data.length / len;
        int y = data.length % len;
        int z = 0;
        if(y!=0){
            z = 1;
        }
        byte[][] arrays = new byte[x+z][];
        byte[] arr;
        for(int i=0; i<x+z; i++){
            arr = new byte[len];
            if(i==x+z-1 && y!=0){
                System.arraycopy(data, i*len, arr, 0, y);
            }else{
                System.arraycopy(data, i*len, arr, 0, len);
            }
            arrays[i] = arr;
        }
        return arrays;
    }

    private static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
        byte[] bcd = new byte[asc_len / 2];
        int j = 0;
        for (int i = 0; i < (asc_len + 1) / 2; i++) {
            bcd[i] = asc_to_bcd(ascii[j++]);
            bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
        }
        return bcd;
    }

    public static byte asc_to_bcd(byte asc) {
        byte bcd;
        if ((asc >= '0') && (asc <= '9'))
            bcd = (byte) (asc - '0');
        else if ((asc >= 'A') && (asc <= 'F'))
            bcd = (byte) (asc - 'A' + 10);
        else if ((asc >= 'a') && (asc <= 'f'))
            bcd = (byte) (asc - 'a' + 10);
        else
            bcd = (byte) (asc - 48);
        return bcd;
    }


    //base64 解密
    public static byte[] decryptBASE64(String key) throws Exception{
        return (new BASE64Decoder()).decodeBuffer(key);
    }

    //base64 加密
    public static String encryptBASE64(byte[] key)throws Exception{
        return (new BASE64Encoder()).encodeBuffer(key);
    }




    /**
     * 产生签名
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        // 解密由base64编码的私钥
        byte[] keyBytes = decryptBASE64(privateKey);

        // 构造PKCS8EncodedKeySpec对象
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);

        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

        // 取私钥对象
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(priKey);
        signature.update(data);

        return encryptBASE64(signature.sign());
    }

    /**
     * 验证签名
     * @param data
     * @param publicKey
     * @param sign
     * @return
     * @throws Exception
     */
    public static boolean verify(byte[] data, String publicKey, String sign)
            throws Exception {

        // 解密由base64编码的公钥
        byte[] keyBytes = decryptBASE64(publicKey);

        // 构造X509EncodedKeySpec对象
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

        // 取公钥匙对象
        PublicKey pubKey = keyFactory.generatePublic(keySpec);

        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(pubKey);
        signature.update(data);

        // 验证签名是否正常
        return signature.verify(decryptBASE64(sign));
    }
    public static String stringToAscii(String value)
    {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(i != chars.length - 1)
            {
                sbu.append((int)chars[i]).append(",");
            }
            else {
                sbu.append((int)chars[i]);
            }
        }
        return sbu.toString();
    }
}
