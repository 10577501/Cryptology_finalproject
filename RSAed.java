package rsa;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.security.Key;

import static rsa.Test.generateKeyPair;

public class RSAed extends Application {
  String publickey;//密钥对
  String privatekey;
  Key[] keypair;

  String signpublickey;//数字签名
  String signprivatekey;
  Key[] signkeypair;
  String sign;

  String osignpublickey;//Oscar数字签名
  String osignprivatekey;
  Key[] osignkeypair;
  byte[]bytes;

  rsa.RSA rsa = new RSA();
  Test rsatest = new Test();

  public Label titlea = new Label();
  public Label titleb = new Label();

  public Button confirma = new Button("加密");
  public Button signa = new Button("签名");
  public Button confirmb = new Button("解密");
  public Button signb = new Button("验证");

  public Button btngen = new Button("生成密钥对");
  public Button btnsign = new Button("乱签一气");
  public Button btnexit = new Button("退出");
  public Button btnclear = new Button("清空");
  public Button btnback = new Button("上一步");

  TextField tfa = new TextField();//加密文本
  TextField tfb = new TextField();//解密文本

  TextArea taa = new TextArea();//加密结果
  TextArea tab = new TextArea();//解密结果

  public static void main(String[] args) {
    launch(args);
  }
  Stage stage=new Stage();
  public void showWindow()throws Exception{start(stage);}
  @Override
  public void start(Stage primaryStage) {
    BorderPane mainPane = new BorderPane();

    Separator separator1 = new Separator();//分隔符
    separator1.setOrientation(Orientation.VERTICAL);

    VBox vboxa = new VBox();
    vboxa.setSpacing(10);
    titlea.setWrapText(true);
    vboxa.getChildren().addAll(titlea,new Label("Alice的明文："),tfa,
      new Label("加密后的结果："),taa,confirma,signa);

    Tooltip tooltip = new Tooltip();
    tooltip.setText("要先加密才能解密噢！");
    confirma.setTooltip(tooltip);
    VBox vboxb = new VBox();
    vboxb.setSpacing(10);
    titleb.setWrapText(true);
    vboxb.getChildren().addAll(titleb,new Label("Bob的密文："),tfb,
      new Label("解密后的结果："),tab,confirmb,signb);

    HBox hbox = new HBox();
    hbox.setSpacing(10);
    hbox.setPadding(new Insets(10,20,10,20));
    hbox.setAlignment(Pos.BOTTOM_RIGHT);
    hbox.getChildren().addAll(btngen,btnsign,btnback,btnclear,btnexit);
    mainPane.setBottom(hbox);

    HBox hboxab = new HBox();
    hboxab.setSpacing(10);
    hboxab.getChildren().addAll(vboxa,separator1,vboxb);
    hboxab.setPadding(new Insets(10,10,10,10));
    mainPane.setCenter(hboxab);

    mainPane.isVisible();

    //内容显示区域
    Scene scene = new Scene(mainPane,850,550, Color.WHITE);
    primaryStage.setTitle("RSA算法教学演示平台");
    primaryStage.setScene(scene);
    primaryStage.show();

    confirma.setDisable(true);//还没生成密钥对前不能加解密
    confirmb.setDisable(true);//还没加密之前不能解密
    signb.setDisable(true);//还没签名不能验证

    rsa.animate("在学习了公钥私钥生成的基本原理后，现在你是Alice，\n填写你想要只告诉Bob的明文，并点击下方按钮用公钥开始加密吧！\nPS:试试生成一个比较安全的数字签名",titlea).play();
    rsa.animate("在学习了公钥私钥生成的基本原理后，然后你又变回了Bob，收到了\n一则密文，但不知道是不是发给你的，点击下方按钮用私钥开始解密吧！\nPS:首先生成一个比较安全的密钥对",titleb).play();
    btngen.setOnAction(event -> {
      confirma.setDisable(false);
      try{
        keypair=rsatest.generateKeyPair();
        publickey=rsatest.encryptBASE64(keypair[0].getEncoded());
        privatekey=rsatest.encryptBASE64(keypair[1].getEncoded());
        taa.appendText("已生成公钥-加密明文："+keypair[0]+"\n");
        taa.appendText("Bob的公钥："+publickey+"\n");
        tab.appendText("已生成私钥-解密密文:"+keypair[1]+"\n");
        tab.appendText("Bob的私钥："+privatekey+"\n");
      }catch (Exception e)
      {
        e.getStackTrace();
      }
    });

    confirma.setOnAction(event -> {//确定加密文本
      if(isLetterDigit((tfa.getText().trim()))){//输入的文本合乎规格 记得改条件
        confirmb.setDisable(false);//可以解密
        String msg = tfa.getText().trim();
        String crypmsg="";
        taa.appendText("开始加密文本："+msg+"\n");
        confirma.setDisable(true);//加密过程中不能再按加密按钮
        tfa.setDisable(true);//不能再改变文本框内容
        btnclear.setDisable(true);//不能再改变文本框内容

        /*RSA加密算法*/
        try{
          crypmsg=rsatest.encrypt(msg,keypair[0]);// 生成的密文
        }catch (Exception e)
        {
          e.getStackTrace();
        }

        if(true)//加密成功后 记得改条件
        {
          taa.appendText("加密成功！"+"\n"+"要加密的文本为："+msg+"\n");//显示原来的加密文本
          taa.appendText("加密结果为："+crypmsg+"\n");//显示加密结果
          taa.appendText("提示：可以直接鼠标选中文本复制到密文文本框处"+"\n");//显示加密结果
          confirma.setDisable(false);//可以再按加密按钮
          tfa.setDisable(false);//可以再输入文本框内容
          btnclear.setDisable(false);//改变文本框内容
        }
      }else{//输入的文本不合规格
        taa.appendText("输入的文本不合规格，请重新输入！仅限数字与英文字母。"+"\n");
      }
    });

    confirmb.setOnAction(event -> {//确定解密文本
      if(isLetterDigit((tfb.getText().trim()))){//输入的文本合乎规格
        String msg = tfb.getText().trim();
        String plainmsg="";
        tab.appendText("开始解密文本："+msg+"\n");
        confirmb.setDisable(true);//解密过程中不能再按解密按钮
        tfb.setDisable(true);//不能改变文本框内容
        btnclear.setDisable(true);//不能再改变文本框内容

        /*RSA解密算法*/
        try{

          plainmsg= rsatest.decrypt(msg,keypair[1]);// 解密密文
        }catch (Exception e)
        {
          e.getStackTrace();
        }
        if(true)//解密成功后 记得改条件
        {
          tab.appendText("解密成功！"+"\n"+"要解密的文本为："+msg+"\n");//显示原来的解密文本
          tab.appendText("解密结果为："+plainmsg+"\n");//显示解密结果
          tab.appendText("提示：可以直接鼠标选中文本复制到明文文本框处"+"\n");
          confirmb.setDisable(false);//可以按解密按钮
          tfb.setDisable(false);//可以继续输入文本了
          btnclear.setDisable(false);//不能再改变文本框内容
        }
      }else{
        tab.appendText("输入的文本不合规格，请重新输入！仅限数字与英文字母"+"\n");
      }
    });
    signa.setOnAction(event -> {//数字签名
      try{
        signkeypair=rsatest.generateKeyPair();
        signpublickey=rsatest.encryptBASE64(signkeypair[0].getEncoded());
        signprivatekey=rsatest.encryptBASE64(signkeypair[1].getEncoded());
        taa.appendText("数字签名-Alice的私钥："+signprivatekey+"\n");
        tab.appendText("数字签名-Alice的公钥："+signpublickey+"\n");
        bytes=(tfa.getText().trim()).getBytes();
        // 产生签名
        sign = rsatest.sign(bytes,signprivatekey);
        taa.appendText("Alice对这则消息所生成的数字签名为："+sign+"\n");
        signb.setDisable(false);
      }catch (Exception e)
      {
        e.getStackTrace();
      }
    });
    signb.setOnAction(event -> {
      try{
        // 验证签名
        boolean status = rsatest.verify(bytes, signpublickey,sign);
        if(status)
        {
          tab.appendText("签名真的是Alice的！"+"\n");
        }else
        {
          tab.appendText("签名好像是伪造的…"+"\n");
          tab.appendText("PS：Bob拿的是Alice签名的公钥进行验证，只有Alice的正确签名才能配对"+"\n");
        }
      }catch (Exception e)
      {
        e.getStackTrace();
      }
    });
    btnsign.setOnAction(event -> {
      try{
        osignkeypair=rsatest.generateKeyPair();
        osignpublickey=rsatest.encryptBASE64(osignkeypair[0].getEncoded());
        osignprivatekey=rsatest.encryptBASE64(osignkeypair[1].getEncoded());
        taa.appendText("数字签名-Oscar的私钥："+osignprivatekey+"\n");
        tab.appendText("数字签名-Oscar的公钥："+osignpublickey+"\n");
        bytes=(tfa.getText().trim()).getBytes();
        // 产生签名
        sign = rsatest.sign(bytes,osignprivatekey);
        taa.appendText("Oscar对这则消息所生成的数字签名为："+sign+"\n");
        signb.setDisable(false);
      }catch (Exception e)
      {
        e.getStackTrace();
      }
    });

    taa.selectionProperty().addListener((observable, oldValue, newValue) -> {
      //只有当鼠标拖动选中了文字才复制内容 直接复制TextArea的内容到TextField里
      if(!taa.getSelectedText().equals(""))//可以完成拖动加密结果到要输入的密文文本框处
        tfb.setText(taa.getSelectedText());
    });

    tab.selectionProperty().addListener((observable, oldValue, newValue) -> {
      //只有当鼠标拖动选中了文字才复制内容 直接复制TextArea的内容到TextField里
      if(!tab.getSelectedText().equals(""))//可以完成拖动解密结果到要输入的明文文本框处
        tfa.setText(tab.getSelectedText());
    });

    btnback.setOnAction(event -> {//切换原来的窗口
      stage.hide();
    });

    btnclear.setOnAction(event -> {
      taa.setText("");
      tab.setText("");
      tfa.setText("");
      tfb.setText("");
    });

    btnexit.setOnAction(event -> {
      System.exit(0);
    });

  }
  public static boolean isLetterDigit(String str) {
    String regex = "^[a-z0-9A-Z]+$";
    return str.matches(regex);
  }

}
