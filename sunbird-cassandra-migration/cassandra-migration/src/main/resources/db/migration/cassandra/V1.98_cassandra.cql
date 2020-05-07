INSERT INTO sunbird.role(id, name, rolegroupid, status) VALUES('COURSE_CREATOR', 'Course Creator', ['COURSE_CREATOR'], 1);
INSERT INTO sunbird.role_group(id, name, url_action_ids) VALUES('COURSE_CREATOR', 'Course Creator', ['courseCreator']);
INSERT INTO sunbird.email_template(name,template) VALUES('verifyPhoneOtpTemplateWard', 'Your ward has requested for registration on $installationName using this email ID. Use OTP $otp to agree and create the account. This is valid for $otpExpiryInMinutes minutes only.');
