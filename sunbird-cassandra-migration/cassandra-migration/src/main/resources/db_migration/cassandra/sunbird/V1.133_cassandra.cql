insert into sunbird.email_template(name,template) values ('otpPhoneResetPasswordTemplate_nic', 'NCERT: OTP to reset your password on $installationName is $otp. This is valid for $otpExpiryInMinutes minutes only.');

insert into sunbird.email_template(name,template) values ('verifyPhoneOtpTemplate_nic', 'NCERT: OTP to verify your phone number on $installationName is $otp. This is valid for $otpExpiryInMinutes minutes only.');

insert into sunbird.email_template(name,template) values ('migrate_user_template','You can now access your $installationName state teacher account using $phone. Please log out and login once again to see updated details.');

insert into sunbird.email_template(name,template) values ('migrate_user_template_nic','NCERT: You can now access your $installationName state teacher account using $phone. Please log out and login once again to see updated details.');

INSERT INTO sunbird.email_template(name,template) VALUES('verifyPhoneOtpTemplateWard', 'Your ward has requested for registration on $installationName using this phone number. Use OTP $otp to agree and create the account. This is valid for $otpExpiryInMinutes minutes only.');

INSERT INTO sunbird.email_template(name,template) VALUES('verifyPhoneOtpTemplateWard_nic', 'NCERT: Your ward has requested for registration on $installationName using this phone number. Use OTP $otp to agree and create the account. This is valid for $otpExpiryInMinutes minutes only.');

INSERT INTO sunbird.email_template(name,template) VALUES('welcomeSmsTemplate', 'Welcome to $instanceName. Your user account has now been created. Click on the link below to #if ($setPasswordLink) set a password #else verify your email ID #end and start using your account:$newline$link');

INSERT INTO sunbird.email_template(name,template) VALUES('welcomeSmsTemplate_nic', 'NCERT: Welcome to $instanceName. Your user account has now been created. Click on the link below to #if ($setPasswordLink) set a password #else verify your email ID #end and start using your account:$newline$link');