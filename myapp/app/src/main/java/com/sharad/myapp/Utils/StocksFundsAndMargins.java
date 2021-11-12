package com.sharad.myapp.Utils;

public class StocksFundsAndMargins {
    private Data data;

    public Data getData() {
        return data;
    }

    public class Data {
        private Equity equity;
        private Commodity commodity;

        public Equity getEquity() {
            return equity;
        }

        public Commodity getCommodity() {
            return commodity;
        }

        public class Equity {
            public boolean enabled;
            public float net;

            public boolean isEnabled() {
                return enabled;
            }

            public float getNet() {
                return net;
            }
        }

        public class Commodity {
            public boolean enabled;
            public float net;

            public boolean isEnabled() {
                return enabled;
            }

            public float getNet() {
                return net;
            }
        }

    }
}

