package rsa;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.net.URI;
import java.util.Random;


public class RSA extends Application {
  int intp;//p
  int intq;//q
  int intN;
  int intr;
  int inte;
  int intd;
  rsa.RSAed second;

  public static void main(String[] args) {
    launch(args);
  }
  Test rsa = new Test();
  public Label title = new Label();

  public Label titlelearn = new Label("密码学小知识");
  public Label learn = new Label("  RSA公开密钥密码体制是一种使用不同的加密密钥与解密密钥，“由已知加密密钥推导出解密密钥在计算上是不可行的”密码体制。\n" +
    "\n" + "  在公开密钥密码体制中，加密密钥（即公开密钥）PK是公开信息，而解密密钥（即秘密密钥）SK是需要保密的。加密算法E和解密算法D也都是公开的。虽然解密密钥SK是由公开密钥PK决定的，但却不能根据PK计算出SK。\n " +
    "\n" +
    "  欧拉定理是RSA算法的核心。任意给定正整数n，计算在小于等于n的正整数之中，有多少个与n构成互质关系。计算这个值的方法叫做欧拉函数，以φ(n)表示。"+"\n\n"+"  如φ(8)=4，应为在1到8之中，与8形成互质关系的有1，3，5，7共4" +
    "个正整数。" +"\n"+"\n" +
     "  如果两个正整数a和n互质，则n的欧拉函数φ(n)可以让下面的式子成立：即是a的φ(n)次方减去1，被n整除。"+"\n"+"\n"+
    "  比如，3和7互质，φ(7)=6，(3^6-1)/7=104。");
  public Hyperlink link = new Hyperlink("了解更多欧拉函数的原理……");
  //超文本链接

  public Label warning = new Label("P 和 Q 不符合规格，请重新输入：P 、Q不能相等，且均为素数。");
  public Label textn = new Label();//"计算N = pq"
  public Label textr = new Label();//"根据欧拉函数，计算 r=φ(p)φ(q)=(p-1)(q-1)"
  public Label texte = new Label();//"程序随机选择一个小于r的整数e,且e与r互质"
  public Label textd = new Label();//"求得e关于r的模反元素d, 使得ed≡1(mod r)"
  public Label textkey = new Label();//"(N，e)是公钥，(N，d)是私钥"

  public TextField p= new TextField();//生成公钥私钥的p
  public TextField q= new TextField();//生成公钥私钥的q

  public Button btnn = new Button("第一步：求N");
  public Button btnr = new Button("第二步：求r");
  public Button btne = new Button("第三步：求e");
  public Button btnd = new Button("第四步：求d");
  public Button btnkey = new Button("第五步：求公钥和私钥");

  public Button btnconfirmpandq = new Button("确定");
  public Button btnnext = new Button("下一步");
  public Button btnclear = new Button("清空");
  public Button btnexit = new Button("退出");

  @Override
  public void start(Stage primaryStage) {
    BorderPane mainPane = new BorderPane();//大的框架
    //内容显示区域
    Font font = Font.font("songti", 14);//字体
    titlelearn.setFont(font);

    second=new RSAed();//新窗口

    Tooltip tooltip = new Tooltip();
    tooltip.setText("点我试试看");//密码学小知识的工具提示 放上去就会有提示 点击显示内容
    titlelearn.setTooltip(tooltip);
    learn.setVisible(false);
    titlelearn.setOnMouseClicked(event -> {//欧拉函数的动态显示
      titlelearn.setFont(Font.font(null, FontWeight.BOLD, 14));
      titlelearn.setTextFill(Color.BLUE);
      learn.setVisible(true);
      learn.setFont(Font.font(null, FontWeight.LIGHT, 13));
    });
    learn.setOnMouseMoved(event -> {
      learn.setFont(Font.font(null, FontWeight.LIGHT, 13));
      learn.setTextFill(Color.BLUE);
    });
    learn.setOnMouseExited(event -> {
      learn.setFont(Font.font(null, FontWeight.LIGHT, 13));
      learn.setTextFill(Color.BLACK);
    });

    Separator separator = new Separator();//分隔符
    separator.setOrientation(Orientation.VERTICAL);

    HBox hBoxwarn = new HBox();//确定按钮+提示信息
    hBoxwarn.setSpacing(10);//各控件之间的间隔
    //hBoxwarn.setPadding(new Insets(0,10,0,0));
    hBoxwarn.getChildren().addAll(warning,btnconfirmpandq);
    hBoxwarn.setAlignment(Pos.CENTER);

    HBox hboxp = new HBox();
    hboxp.getChildren().addAll(new Label("输入一个素数P："),p);
    HBox hboxq = new HBox();
    hboxq.getChildren().addAll(new Label("输入一个素数Q："),q);

    VBox vBox = new VBox();//输入变量+求出变量
    vBox.setSpacing(10);//各控件之间的间隔
    vBox.setPrefWidth(400);
    vBox.setPadding(new Insets(20,10,0,10));//VBox面板中的内容距离四周的留空区域
    vBox.getChildren().addAll(hboxp,
      hboxq,btnn,textn,btnr,textr,btne,texte,btnd,textd,btnkey,textkey);
    mainPane.setCenter(vBox);

    learn.setWrapText(true);//支持换行
    learn.setPrefWidth(400);//设置宽度
    VBox vBoxlearn = new VBox();//右栏知识展示区
    vBoxlearn.setSpacing(10);//各控件之间的间隔
    //VBox面板中的内容距离四周的留空区域
    vBoxlearn.setPadding(new Insets(20,10,0,10));
    vBoxlearn.getChildren().addAll(titlelearn,learn,link);

    HBox hBoxall = new HBox();//左中右
    hBoxall.setPadding(new Insets(20,0,0,0));
    hBoxall.getChildren().addAll(vBox,separator,vBoxlearn);
    mainPane.setCenter(hBoxall);

    HBox hBox = new HBox();//底部按钮区域
    hBox.setSpacing(10);
    hBox.setPadding(new Insets(10,20,10,20));
    hBox.setAlignment(Pos.BOTTOM_RIGHT);
    hBox.getChildren().addAll(hBoxwarn,btnnext,btnclear,btnexit);
    mainPane.setBottom(hBox);

    HBox titlebox = new HBox();
    titlebox.setPadding(new Insets(5,20,0,20));
    titlebox.setAlignment(Pos.CENTER);
    titlebox.getChildren().addAll(title);
    title.setWrapText(true);
    mainPane.setTop(titlebox);

    Scene scene = new Scene(mainPane,850,550,Color.WHITE);
    primaryStage.setTitle("RSA算法教学演示平台");
    primaryStage.setScene(scene);
    primaryStage.show();

    warning.setVisible(false);//错误信息 先隐藏
    btnnext.setDisable(true);//下一步按钮在确定P和Q之前先禁用
    btnn.setDisable(true);//在确定P和Q之前先禁用
    btnr.setDisable(true);//在确定P和Q之前先禁用
    btne.setDisable(true);//在确定P和Q之前先禁用
    btnd.setDisable(true);//在确定P和Q之前先禁用
    btnkey.setDisable(true);//在确定P和Q之前先禁用

    animate("Alice希望通过RSA加密算法将明文X加密成密文Y并发送给Bob。\n现在你是Bob，你希望接收到来自Alice的密文Y并成功解密读出明文X。\n现在，先来生成一对公钥和私钥吧！",title).play();//介绍文本的动态显示

    btnclear.setOnAction(event -> {
      p.setText("");
      q.setText("");
    });

    btnexit.setOnAction(event -> {
      System.exit(0);
    });

    btnconfirmpandq.setOnAction(event -> {//判断p和q是否为素数 而且是否相同
      /*判断PQ是否是素数且是否相等的代码*/
      intp =Integer.parseInt( p.getText().trim());
      intq =Integer.parseInt(  q.getText().trim());

      if(rsa.isPrime(intp)&&rsa.isPrime(intq)&&intp!=intq){//pq格式正确，计算e和r并进行相应显示
        warning.setVisible(false);//不显示错误信息
        btnconfirmpandq.setDisable(true);//不能再重复确认PQ
        btnclear.setDisable(true);//不能清空PQ
        btnn.setDisable(false);//可以求N
        p.setDisable(true);//不能更改P
        q.setDisable(true);//不能更改Q
      }else{//pq格式错误
        warning.setVisible(true);//显示错误信息
      }
    });

    btnn.setOnMouseClicked(event -> {//求N
      /*求N的代码*/
      intN= intq*intp;

      btnn.setText("公共模数N= "+intN);
      animate("计算N = pq",textn).play();//N的导出条件的动态显示
      btnr.setDisable(false);//可以继续计算r
    });

    btnr.setOnMouseClicked(event -> {//求r
      /*求r的代码*/
      intr=(intp-1)*(intq-1);

      btnr.setText("欧拉函数r= "+intr);
      animate("根据欧拉函数，计算 r=φ(p)φ(q)=(p-1)(q-1)",textr).play();//导出条件的动态显示
      btne.setDisable(false);//可以继续计算e
    });

    btne.setOnMouseClicked(event -> {//求e
      /*求e的代码*/
      Random ran=new Random();
      //rand.nextInt(MAX-MIN+1)+MIN; 将被赋值为一个MIN和MAX范围内的随机数
      inte=ran.nextInt(intr-2)+2;//e的范围是[2，r-1]
      while(!rsa.isCoprime(intr,inte)||(intr==inte)){//判断e和φ(n)是否互为质数
        ran=new Random();
        inte=ran.nextInt(intr-2)+2;
      }

      btne.setText("公钥e= "+inte);//
      animate("程序随机选择一个小于r的整数e，且e与r互质",texte).play();//导出条件的动态显示
      btnd.setDisable(false);//可以继续计算d
    });

    btnd.setOnMouseClicked(event -> {//求d
      /*求d的代码*/
      intd=rsa.myEuclid(inte,intr);

      btnd.setText("私钥d= "+intd);//记得改一下d的结果
      animate("求得e关于r的模反元素d, 使得ed ≡ 1(mod r)",textd).play();//导出条件的动态显示
      btnkey.setDisable(false);//可以继续计算key公钥私钥
    });

    btnkey.setOnMouseClicked(event -> {//求key
      /*求key的代码*/

      btnnext.setDisable(false);//可以点击下一步按钮
      btnkey.setText("公钥= ("+intN+","+inte+")，私钥= ("+intN+","+intd+")");//
      animate("(N，e)是公钥，(N，d)是私钥",textkey).play();//导出条件的动态显示
    });

    btnnext.setOnAction(event -> {
      try{
        changeWindow();//显示下一步的窗口 传参
      }catch (Exception e)
      {
        e.getStackTrace();
      }
    });

    link.setOnAction(event -> {//欧拉函数百度百科的超链接
      try{
        Desktop.getDesktop().browse(new URI("https://baike.baidu.com/item/%E6%AC%A7%E6%8B%89%E5%87%BD%E6%95%B0/1944850?fr=aladdin"));
      }catch (Exception e)
      {
        e.getStackTrace();
      }
    });
  }
  public void changeWindow() throws Exception {//切换窗口的函数
    second.showWindow();
  }

  public Animation animate(String content,Label text)//动态显示文本的函数
  {
    Animation animation = new Transition() {
      {
        setCycleDuration(Duration.millis(2000));
      }
      protected void interpolate(double frac) {
        final int length = content.length();
        final int n = Math.round(length * (float) frac);
        text.setText(content.substring(0, n));
      }
    };
    return animation;
  }
}
