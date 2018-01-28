//import java.util.Arrays;
//
//import org.apache.spark.SparkConf;
//import org.apache.spark.api.java.JavaPairRDD;
//import org.apache.spark.api.java.JavaRDD;
//import org.apache.spark.api.java.JavaSparkContext;
//import org.apache.spark.api.java.function.FlatMapFunction;
//import org.apache.spark.api.java.function.PairFunction;
//import org.apache.spark.api.java.function.VoidFunction;
//
//import scala.Function2;
//import scala.Tuple2;
//
///**
// * Created by THink on 2016/6/29.
// * 1 该类是使用spark本地模式计算wordcount的实例
// */
//public class WordCountLocal {
//    public static void main(String[] args, org.apache.spark.api.java.function.Function2<Integer, Integer, Integer> ) {
//        // 1 先创建sparkconf对象
//        SparkConf conf=new SparkConf();
//        conf.setAppName("word count local");
//        conf.setMaster("local");
//        // 2 创建javasparkcontext对象
//        JavaSparkContext context=new JavaSparkContext(conf);
//        // 3 读取本地文件
//        JavaRDD<String>lines=context.textFile("d://test.txt");
//        // 4 按行读取数据,并按空格进行拆分
//        JavaRDD<String>words=lines.flatMap(new FlatMapFunction<String, String>() {
//            public Iterable<String> call(String s) throws Exception {
//                return Arrays.asList(s.split(" "));
//            }
//        });
//        // 5 开始把数据结构转换成map类型(key,1)
//        JavaPairRDD<String,Integer>word=words.mapToPair(new PairFunction<String, String, Integer>() {
//            public Tuple2<String, Integer> call(String s) throws Exception {
//                return new Tuple2<String, Integer>(s,1);
//            }
//        });
//        // 6 按照key进行聚合计算
//        word.filter(new org.apache.spark.api.java.function.Function2() {
//
//            @Override
//            public Object call(Object v1, Object v2) throws Exception {
//                // TODO Auto-generated method stub
//                return null;
//            }
//        });
//        // 7 循环打印结果
//        result.foreach();
//
//       
//         
//    }
//}
