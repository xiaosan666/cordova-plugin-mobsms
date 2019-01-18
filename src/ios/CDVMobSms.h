//
//  CDVAMapLocation.h
//  Created by tomisacat on 16/1/8.
//
//

#import <Cordova/CDVPlugin.h>
#import <SMS_SDK/SMSSDK.h>



@interface CDVMobSms : CDVPlugin

// @property (nonatomic, strong) AMapLocationManager *locationManager;
@property(nonatomic,strong)NSString *_phone;
@property(nonatomic,strong)NSString *_verifyCodeField;
@property(nonatomic,strong)NSString *currentCallbackId;


-(void) INITIALIZE:(CDVInvokedUrlCommand*)command;

-(void) RequestVerifyCode:(CDVInvokedUrlCommand*)command;

-(void) SubmitVerifyCode:(CDVInvokedUrlCommand*)command;


@end
