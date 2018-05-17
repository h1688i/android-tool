 ****Android後臺服務常駐守護機制****

---
##### ****基於在Strategy App專案設計需求上,需要全天候接收Server端發送的訊息,所以必須在使用者離開APP時能具備和Server持續保持連線並不定時接收訊息後,將訊息推送至系統介面顯示給使用者看的功能,這個服務特性在於實現了兩個服務綁在其中一方服務被終止時,存活的一方可以重新啟動被終止的服務。****
---
****後臺常駐服務類別關係圖:****
* ##### 圖 1. 
![](BackendServiceClassDiagram.png)
* ##### **圖 1.** 說明Strategy後臺服務基於Daemon類別為基礎,由CoreService,PushService繼承Daemon服務,並從PushServic延伸Client遠端服務通訊與Polling系統輪詢功能。
---
****後台服務流程圖:****
* ##### **圖 2.** 
![](android_backend_push_msg_service.png)

* ##### **圖 2.** 說明使用android service元件做為移動端與遠端建立通訊接收遠端消息流程。
* 圖 2. 分為三個部分
    * 客戶端介面
        * 推送服務經由客戶端介面啟動,當PushService接收到遠端推送訊息時,在將訊息推送到前端顯示。
    * 系統後端服務
        * CoreService,PushService兩個互相綁定,當其中一方退出,由存活的另一方將重新啟動退出的服務。
        * PushService
            * 系統輪詢:每週期循環喚醒service。
            * Client:登入伺服器後每週期向遠端確認連線,並等待接收遠端訊息。
    * 遠端伺服器
        * 移動端即時訊息來源。
---
#####  **service保護機制** : 經由下述2點保護service服務不被永久終止
        1. START_STICKY 
        2. 系統廣播
              重新開機
              連接電源
              斷開電源
              解鎖螢幕
              網路變化

        PS:最後不要忘記設定自動啟動管理為開啟,或是引導使用者開啟此功能
---
 ##### 你可以在 **code/v1.0** 裡找到實現上述程序的代碼,這個版本裡代碼有部分需要你修改成符合你專案上的需求,因為有些部分是我自己的專案上的需求,這些部分代碼會在下一個版本中優化使他成為一個獨立的小系統,代碼中註釋掉的部分是在下專案開發需要的方法,在你的專案中可能不會需要他,所以幫你註釋掉了,註釋的代碼有:

 * ***PushService.java***

   * ##### void pushMsg(String title, String text) 你可能不會需要他

 * ***Client.java***

   * ##### String ntpTime() 你可能不會需要他
   * ##### work(char action,String text) 這裡你可能會想要實現自己的做法
   * ##### void checkLogin(String text) 你可能不會需要他
   * ##### void checkConnection() 你可能不會需要他
   * ##### void pushMsg(String text) 你可能不會需要他

* ***daemon.code.v1.0 package 各類別功能如下:***
  * ##### Daemon.java 服務守護父類,實現了服務綁定,服務離線重啟 
  * ##### PushService.java 推送服務,實現了訊息推送,遠端通訊
  * ##### CoreService.java 核心服務,作為綁定對象
  * ##### polling.java 系統輪詢
  * ##### Client.java 通訊
  * ##### IO.java 檔案讀寫
  * ##### Device.java 裡面有網路狀態相關,檢查服務是否運行方法
  * ##### SystemBroadcastReceiver.java 系統廣播接收
  * ##### NotificationUI.java 訊息通知 X
  * ##### User.java 使用者訊息加解密存取 X
  * ##### AES.java 訊息加密 X
  * ##### ServiceManager.java 服務啟動相關功能 X
 
#### 後面打 X 表示你可能不會需要的部分,剩下部分足以實現後台長駐服務,遠端通訊,與系統輪詢功能
---
##### 你可能會需要在AndroidManifest.xm中做以下設定:

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />

    <!-- 核心服務  -->
    
    <service android:name="com.attraxus.service.CoreService"
    	     android:process=":core"
             android:exported="false">
    </service>
    <!-- 推送服務  -->
    <service android:name="com.attraxus.service.PushService"  
             android:process=":push"       	
             android:exported="false">
        </service>
    <!-- 系統廣播  -->
    <receiver android:name="com.attraxus.service.SystemBroadcastReceiver"
              android:exported="true">
		<intent-filter>
		    <action android:name="android.intent.action.USER_PRESENT" />
		    <action android:name="android.intent.action.ACTION_POWER_CONNECTED"/>
		    <action android:name="android.intent.action.ACTION_POWER_DISCONNECTED"/>
		    <action android:name="android.intent.action.BOOT_COMPLETED"/>
		    <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
    	</intent-filter>
	</receiver>

##### 以上在 
        小米4 Android6.0
        Asua Zenfone Android4.4.2

##### 測試在失去後臺服務後可以被重新啟動,在網路狀態改變時能夠重新連接遠端服務,並在每一分鐘確認一次遠端連線,達到省電與保持遠端連線,並在無網路連線狀態及intent動作不明情況中服務將處於待機,等待下一次系統輪詢,使後臺服務不浪費裝置珍貴cpu資源
---
##### 有任何問題歡迎交流 : **<hsu-chia-chang@hotmail.com>**
---
