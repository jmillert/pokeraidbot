package pokeraidbot.ocr;

public class OCRAnswer {
    private String time;
    private String countdown;
    private String gymName;
    private String pokemon;
    private Type type;
    private String source;


    public String getTime() {
        return time;
    }

    public String getCountdown() {
        return countdown;
    }

    public String getGymName() {
        return gymName;
    }

    public String getPokemon() {
        return pokemon;
    }

    public Type getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public static final class OCRAnswerBuilder {
        String time;
        String countdown;
        String gymName;
        String pokemon;
        Type type;
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

        public OCRAnswerBuilder withType(Type type) {
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
                    (Type.BOSS.equals(type) && time == null || countdown == null || gymName == null)) {
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

    public enum Type {
        BOSS,
        EGG
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OCRAnswer ocrAnswer = (OCRAnswer) o;

        if (time != null ? !time.equals(ocrAnswer.time) : ocrAnswer.time != null) return false;
        if (countdown != null ? !countdown.equals(ocrAnswer.countdown) : ocrAnswer.countdown != null) return false;
        if (gymName != null ? !gymName.equals(ocrAnswer.gymName) : ocrAnswer.gymName != null) return false;
        if (pokemon != null ? !pokemon.equals(ocrAnswer.pokemon) : ocrAnswer.pokemon != null) return false;
        if (type != ocrAnswer.type) return false;
        return source != null ? source.equals(ocrAnswer.source) : ocrAnswer.source == null;
    }

    @Override
    public int hashCode() {
        int result = time != null ? time.hashCode() : 0;
        result = 31 * result + (countdown != null ? countdown.hashCode() : 0);
        result = 31 * result + (gymName != null ? gymName.hashCode() : 0);
        result = 31 * result + (pokemon != null ? pokemon.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Source: " + source + '\n' +
                "Time:   " + time + '\n' +
                "Count:  " + countdown + '\n' +
                "Gym:    " + gymName + '\n' +
                (type == Type.BOSS ?"Pokemon " + pokemon + '\n' : "") +
                "Type:   " + type;
    }

    private static boolean hasNoValue(String str) {
        return str == null || str.isEmpty();
    }
}

