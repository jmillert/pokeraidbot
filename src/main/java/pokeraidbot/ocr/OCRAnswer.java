package pokeraidbot.ocr;

public class OCRAnswer {
    String time;
    String countdown;
    String gymName;
    String pokemon;
    TYPE type;
    String source;

    public static final class OCRAnswerBuilder {
        String time;
        String countdown;
        String gymName;
        String pokemon;
        TYPE type;
        String source;

        private OCRAnswerBuilder() {
        }

        public static OCRAnswerBuilder anOCRAnswer() {
            return new OCRAnswerBuilder();
        }

        public OCRAnswerBuilder withTime(String time) {
            this.time = time;
            return this;
        }

        public OCRAnswerBuilder withCountdown(String countdown) {
            this.countdown = countdown;
            return this;
        }

        public OCRAnswerBuilder withGymName(String gymName) {
            this.gymName = gymName;
            return this;
        }

        public OCRAnswerBuilder withPokemon(String pokemon) {
            this.pokemon = pokemon;
            return this;
        }

        public OCRAnswerBuilder withType(TYPE type) {
            this.type = type;
            return this;
        }

        public OCRAnswerBuilder withSource(String source) {
            this.source = source;
            return this;
        }

        public OCRAnswer build() {
            switch (type) {
                case BOSS:
                    if (hasNoValue(time) || hasNoValue(countdown) || hasNoValue(gymName) || hasNoValue(pokemon)) {
                        throw new IllegalStateException("Unable to build boss answer, missing parameter");
                    }
                    break;
                case EGG:
                    if (hasNoValue(time) || hasNoValue(countdown) || hasNoValue(gymName)) {
                        throw new IllegalStateException("Unable to build egg answer, missing parameter");
                    }
                    break;
                default:
                    throw  new IllegalStateException();
            }

            if(type == null ||
                    (TYPE.BOSS.equals(type) && time == null || countdown == null || gymName == null)) {
                throw new IllegalStateException();
            }
            OCRAnswer oCRAnswer = new OCRAnswer();
            oCRAnswer.time = this.time;
            oCRAnswer.countdown = this.countdown;
            oCRAnswer.gymName = this.gymName;
            oCRAnswer.pokemon = this.pokemon;
            oCRAnswer.type = this.type;
            oCRAnswer.source = this.source;
            return oCRAnswer;
        }
    }

    public enum TYPE {
        BOSS,
        EGG
    }

    @Override
    public String toString() {
        return "Source: " + source + '\n' +
                "Time:   " + time + '\n' +
                "Count:  " + countdown + '\n' +
                "Gym:    " + gymName + '\n' +
                (type == TYPE.BOSS ?"Pokemon " + pokemon + '\n' : "") +
                "Type:   " + type;
    }

    private static boolean hasNoValue(String str) {
        return str == null || str.isEmpty();
    }
}

