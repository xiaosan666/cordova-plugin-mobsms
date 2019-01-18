//
//  CDVAMapLocation.m
//  Created by tomisacat on 16/1/8.
//
//

#import "CDVMobSms.h"
#import <SMS_SDK/SMSSDK.h>

@implementation CDVMobSms





//INITIALIZE
-(void) INITIALIZE:(CDVInvokedUrlCommand*)command{
    NSMutableDictionary *args = [command argumentAtIndex:0];
    NSDictionary *MobConfig = [args objectForKey:@"MobConfig"];
    NSString   *APPKEY  = [MobConfig objectForKey:@"APPKEY"];
    NSString   *APPSECRET  = [MobConfig objectForKey:@"APPSECRET"];
    [self.commandDelegate runInBackground:^{
      [SMSSDK registerApp:APPKEY withSecret:APPSECRET];
    }];
     NSLog(@"初始化成功");
    



}

//RequestVerifyCode
-(void) RequestVerifyCode:(CDVInvokedUrlCommand*)command{
    NSMutableDictionary *args = [command argumentAtIndex:0];
    NSString   *PhoneNumber  = [args objectForKey:@"PhoneNumber"];
    self.currentCallbackId = command.callbackId;
    self._phone = PhoneNumber;

    [self.commandDelegate runInBackground:^{
        [SMSSDK getVerificationCodeByMethod:SMSGetCodeMethodSMS phoneNumber:PhoneNumber
                                                               zone:@"86"
                                                            customIdentifier:nil
                                                             result:^(NSError *error){
           if (!error) {
                NSLog(@"获取验证码成功");
                NSString *status = @"0";
                NSString *message = @"request OK";
                 NSDictionary *resultDic = [NSDictionary dictionaryWithObjectsAndKeys:
                                            status,@"status",
                                            message,@"message",
                               nil];
                 [self successWithCallbackID:self.currentCallbackId messageAsDictionary:resultDic];

            } else {
                NSLog(@"错误信息：%@",error);
                NSString *message = @"RequestVerifyCode";
                NSString *status = [NSString stringWithFormat:@"%ld",error.code];
                NSDictionary *resultDic = [NSDictionary dictionaryWithObjectsAndKeys:
                                            status,@"status",
                                            message,@"message",
                               nil];
                [self failWithCallbackID:self.currentCallbackId messageAsDictionary:resultDic];
                

            }
        }];
     }];
        
}

//SubmitVerifyCode
-(void) SubmitVerifyCode:(CDVInvokedUrlCommand*)command{
    NSMutableDictionary *args = [command argumentAtIndex:0];
    NSString   *VerifyCode  = [args objectForKey:@"VerifyCode"];
    self.currentCallbackId = command.callbackId;
    self._verifyCodeField = VerifyCode;

    [self.commandDelegate runInBackground:^{
        [SMSSDK commitVerificationCode:self._verifyCodeField phoneNumber:self._phone zone:@"86" result:^(SMSSDKUserInfo *userInfo, NSError *error) {

        {
            if (!error)
            {   
                NSLog(@"验证成功");
                NSString *status = @"0";
                NSString *message = @"verify OK";
                 NSDictionary *resultDic = [NSDictionary dictionaryWithObjectsAndKeys:
                                            status,@"status",
                                            message,@"message",
                               nil];
                 [self successWithCallbackID:self.currentCallbackId messageAsDictionary:resultDic];
            }
            else
            {
                NSLog(@"错误信息:%@",error);
                NSString *status = [NSString stringWithFormat:@"%ld",error.code];
                NSString *message = @"SubmitVerifyCode";
                NSDictionary *resultDic = [NSDictionary dictionaryWithObjectsAndKeys:
                                            status,@"status",
                                            message,@"message",
                               nil];
                [self failWithCallbackID:self.currentCallbackId messageAsDictionary:resultDic];

              
            }
        }
      }];
        
     }];
        
}


- (void)successWithCallbackID:(NSString *)callbackID withMessage:(NSString *)message
{
    CDVPluginResult *commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:message];
    [self.commandDelegate sendPluginResult:commandResult callbackId:callbackID];
}

- (void)failWithCallbackID:(NSString *)callbackID withMessage:(NSString *)message
{
    CDVPluginResult *commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:message];
    [self.commandDelegate sendPluginResult:commandResult callbackId:callbackID];
}
- (void)successWithCallbackID:(NSString *)callbackID messageAsDictionary:(NSDictionary *)message
{
    CDVPluginResult *commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:message];
    [self.commandDelegate sendPluginResult:commandResult callbackId:callbackID];
}

- (void)failWithCallbackID:(NSString *)callbackID messageAsDictionary:(NSDictionary *)message
{
    CDVPluginResult *commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:message];
    [self.commandDelegate sendPluginResult:commandResult callbackId:callbackID];
}



@end
