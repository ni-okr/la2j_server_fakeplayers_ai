package net.sf.l2j.botmanager.learning;

/**
 * Базовый класс для функций активации
 * 
 * Этот класс определяет интерфейс для всех функций активации,
 * используемых в нейронных сетях. Каждая функция активации
 * должна реализовывать как саму функцию, так и её производную.
 * 
 * @author AI Assistant
 * @version 3.2
 * @since 2025-09-27
 */
public abstract class ActivationFunction {
    
    /**
     * Применяет функцию активации
     * 
     * @param x входное значение
     * @return результат функции активации
     */
    public abstract double activate(double x);
    
    /**
     * Вычисляет производную функции активации
     * 
     * @param x входное значение
     * @return производная функции активации
     */
    public abstract double derivative(double x);
    
    /**
     * Возвращает название функции активации
     * 
     * @return название функции
     */
    public abstract String getName();
    
    /**
     * Сигмоидальная функция активации
     */
    public static class Sigmoid extends ActivationFunction {
        @Override
        public double activate(double x) {
            // Предотвращаем переполнение
            if (x > 700) return 1.0;
            if (x < -700) return 0.0;
            return 1.0 / (1.0 + Math.exp(-x));
        }
        
        @Override
        public double derivative(double x) {
            double s = activate(x);
            return s * (1.0 - s);
        }
        
        @Override
        public String getName() {
            return "Sigmoid";
        }
    }
    
    /**
     * Гиперболический тангенс
     */
    public static class Tanh extends ActivationFunction {
        @Override
        public double activate(double x) {
            // Предотвращаем переполнение
            if (x > 350) return 1.0;
            if (x < -350) return -1.0;
            return Math.tanh(x);
        }
        
        @Override
        public double derivative(double x) {
            double t = activate(x);
            return 1.0 - t * t;
        }
        
        @Override
        public String getName() {
            return "Tanh";
        }
    }
    
    /**
     * ReLU (Rectified Linear Unit)
     */
    public static class ReLU extends ActivationFunction {
        @Override
        public double activate(double x) {
            return Math.max(0.0, x);
        }
        
        @Override
        public double derivative(double x) {
            return x > 0 ? 1.0 : 0.0;
        }
        
        @Override
        public String getName() {
            return "ReLU";
        }
    }
    
    /**
     * Leaky ReLU
     */
    public static class LeakyReLU extends ActivationFunction {
        private final double alpha;
        
        public LeakyReLU(double alpha) {
            this.alpha = alpha;
        }
        
        public LeakyReLU() {
            this(0.01); // По умолчанию alpha = 0.01
        }
        
        @Override
        public double activate(double x) {
            return x > 0 ? x : alpha * x;
        }
        
        @Override
        public double derivative(double x) {
            return x > 0 ? 1.0 : alpha;
        }
        
        @Override
        public String getName() {
            return "LeakyReLU";
        }
    }
    
    /**
     * ELU (Exponential Linear Unit)
     */
    public static class ELU extends ActivationFunction {
        private final double alpha;
        
        public ELU(double alpha) {
            this.alpha = alpha;
        }
        
        public ELU() {
            this(1.0); // По умолчанию alpha = 1.0
        }
        
        @Override
        public double activate(double x) {
            return x > 0 ? x : alpha * (Math.exp(x) - 1.0);
        }
        
        @Override
        public double derivative(double x) {
            return x > 0 ? 1.0 : alpha * Math.exp(x);
        }
        
        @Override
        public String getName() {
            return "ELU";
        }
    }
    
    /**
     * Softmax функция активации
     */
    public static class Softmax extends ActivationFunction {
        @Override
        public double activate(double x) {
            // Softmax применяется к массиву, поэтому здесь возвращаем x
            return x;
        }
        
        @Override
        public double derivative(double x) {
            // Производная softmax вычисляется по-особому
            return 1.0;
        }
        
        @Override
        public String getName() {
            return "Softmax";
        }
        
        /**
         * Применяет softmax к массиву значений
         * 
         * @param values массив значений
         * @return массив после применения softmax
         */
        public double[] applySoftmax(double[] values) {
            double[] result = new double[values.length];
            double max = Double.NEGATIVE_INFINITY;
            
            // Находим максимальное значение для численной стабильности
            for (double value : values) {
                max = Math.max(max, value);
            }
            
            double sum = 0.0;
            for (int i = 0; i < values.length; i++) {
                result[i] = Math.exp(values[i] - max);
                sum += result[i];
            }
            
            // Нормализуем
            for (int i = 0; i < result.length; i++) {
                result[i] /= sum;
            }
            
            return result;
        }
    }
    
    /**
     * Swish функция активации
     */
    public static class Swish extends ActivationFunction {
        @Override
        public double activate(double x) {
            // Предотвращаем переполнение
            if (x > 700) return x;
            if (x < -700) return 0.0;
            return x / (1.0 + Math.exp(-x));
        }
        
        @Override
        public double derivative(double x) {
            double s = 1.0 / (1.0 + Math.exp(-x));
            return s + x * s * (1.0 - s);
        }
        
        @Override
        public String getName() {
            return "Swish";
        }
    }
    
    /**
     * GELU (Gaussian Error Linear Unit)
     */
    public static class GELU extends ActivationFunction {
        @Override
        public double activate(double x) {
            return 0.5 * x * (1.0 + Math.tanh(Math.sqrt(2.0 / Math.PI) * (x + 0.044715 * x * x * x)));
        }
        
        @Override
        public double derivative(double x) {
            // Упрощенная производная GELU
            double tanh = Math.tanh(Math.sqrt(2.0 / Math.PI) * (x + 0.044715 * x * x * x));
            double sech2 = 1.0 - tanh * tanh;
            return 0.5 * (1.0 + tanh) + 0.5 * x * sech2 * Math.sqrt(2.0 / Math.PI) * (1.0 + 3.0 * 0.044715 * x * x);
        }
        
        @Override
        public String getName() {
            return "GELU";
        }
    }
}
