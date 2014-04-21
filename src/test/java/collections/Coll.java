package collections;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年4月17日 上午11:31:24
 */
public class Coll {

	public static void main(String[] args) {
		List<Integer> c1 = Arrays.asList(1, 2, 3, 4, 5, 6);
		List<Integer> c2 = Arrays.asList(1, 3, 4, 5, 6, 7, 8);
		
		for (Integer i : c1) {
			
//			Collections.sort(list);
			
			int index = Collections.binarySearch(c2, i);
			if(index >= 0) {
				System.out.println(i);
			}
		}
	}
}
