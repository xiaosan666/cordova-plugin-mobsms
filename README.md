# cordova-plugin-mobsms
一款免费发送短信的cordova插件。官网只提供原生sdk：http://mob.com/product/sms  
原插件地址：https://github.com/menglol/cordova-mobsms  我优化了了android代码并做了测试，ios代码未做测试，我看不懂objective-c，请会的大神调试一下

# 在ionic项目中使用
``` html
<ion-content>
  home
  <br>
  <br>
  <button ion-button (click)="init()">初始化短信插件</button>
  <br>
  <br>
  <input type="number" [(ngModel)]="phone" placeholder="输入你的手机号码">
  <button ion-button (click)="requestVerifyCode()">发送验证码</button>
  <br>
  <br>
  <input type="number" [(ngModel)]="verifyCode"  placeholder="输入接受到的验证码">
  <button ion-button (click)="submitVerifyCode()">验证是否输入正确</button>
</ion-content>

```

``` typescript
import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';


@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage {
  cordovaMobSms;

  phone = ''; // 输入手机号
  verifyCode = ''; // 接受到的验证码

  constructor(public navCtrl: NavController) {
  }

  // 发送验证码前需要初始化操作
  init() {
    if (!window['mobsms']) {
      alert('请在真机调试或检查插件是否安装正确');
      return;
    }
    debugger;
    // 申请key： http://dashboard.mob.com/#!/sms/dashboard
    let mobConfig = {
      APPKEY: '29d025c53ffd1',
      APPSECRET: '9d67b36379852a2a5cd3980ca95e44c5'
    };
    this.cordovaMobSms = window['mobsms'].init({MobConfig: mobConfig});
  }

  // 发送验证码
  requestVerifyCode() {
    debugger;
    this.cordovaMobSms.RequestVerifyCode(res => {
      debugger;
    }, err => {
      debugger;
    }, this.phone);
  }

  // 判断验证码是否正确
  submitVerifyCode() {
    debugger;
    this.cordovaMobSms.SubmitVerifyCode(res => {
      debugger;
    }, err => {
      debugger;
    }, this.phone, this.verifyCode);
  }
}


```
