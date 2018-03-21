ALTER TABLE sunbird.user_badge_assertion ADD name text;
ALTER TABLE sunbird.user_badge_assertion ADD image text;
ALTER TABLE sunbird.user_badge_assertion DROP badgeClassName;
ALTER TABLE sunbird.user_badge_assertion DROP badgeClassImage;