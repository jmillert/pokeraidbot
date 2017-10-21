package pokeraidbot.ocr;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;

public class RectangleRepo {
    public static Rectangle getRectangle(RectangleType type, int imageWidth, int imageHeight) {
        switch (type) {
            case EGG_SYSTEM_TIME :
                return new Rectangle(0, 0,
                        imageWidth-1, imageHeight/15);
            case EGG_GYM:
                return new Rectangle(imageWidth/5, imageHeight/17,
                        imageWidth-imageWidth/5, imageHeight/15);
            case EGG_COUNTDOWN:
                int width = imageWidth/3;
                int height = imageHeight/10;
                int upperY = imageHeight/6;
                return new Rectangle((imageWidth-width)/2, upperY, width, height);
            case BOSS_SYSTEM_TIME :
                return new Rectangle(0, 0,
                        imageWidth-1, imageHeight/15);
            case BOSS_GYM:
                return new Rectangle(imageWidth/5, imageHeight/17,
                        imageWidth-imageWidth/5, imageHeight/15);
            case BOSS_COUNTDOWN :
                return new Rectangle((int)(imageWidth*0.7), (int)(imageHeight*0.55),
                        imageWidth/4, imageHeight/10);
            case BOSS_NAME :
                return new Rectangle(0, (int)(imageHeight/4.5), imageWidth-1, (int)(imageHeight/5.5));
            default :
                throw new NotImplementedException();
        }
    }
}

