package BloomFilter;

import java.io.*;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Fruits extends JFrame {

	private static JTextArea textArea;

	public static int bitwiseMod11(int intFruit) {

		int divisor = 15;

		// divisor 2nin katı mı kontrol eder eğer sonuç 0 ise, "divisor" 2'nin bir
		// kuvvetidir yani 2'ye tam bölünebilir.
		if ((divisor & (divisor - 1)) == 0) {
			return intFruit & (divisor - 1);

		}
		int remainder = 0;

		for (int i = 31; i >= 0; i--) { // dividend 'ın her biti boyunca döngüye gir

			remainder <<= 1; // 1 bit sola kay (sayıyı 2 ile çarpma)

			int bit = (intFruit >>> i) & 1; // dividend 'ın i. bitini alır

			remainder |= bit; // Kalanın least significant (en sağdaki) bitini bit olarak ayarla

			// Kalan bölenden büyük veya eşitse, kalandan böleni çıkar ve bölenin i. bitini
			// 1 olarak ayarla
			if (remainder >= divisor) {
				remainder -= divisor;
				intFruit |= (1 << i);
			}
		}
		return remainder;
	}

	public static int hash1(int intFruit) {

		int hash1Value = intFruit; // girdinin kendisini başlangıç değeri olarak atar.
		int prime = 31; // int 4 byte yani 32 bit fakat çakışmaları (collision) önlemek için 31 asal
						// sayı değerini sabit aldık.

		for (int i = 0; i < 4; i++) { // int 4 byte olduğu için 4 kez döngüye giriyoruz.

			int currentByte = (hash1Value >> (i * 8)) & 0xff;
			hash1Value ^= (hash1Value << 5) | (hash1Value >>> 27); // her byte için bitwise XOR ve sola kaydırma
																	// işlemleri yapılır.
			hash1Value ^= (prime * currentByte);
		}

		/*
		 * Değişkenin değerini kendisiyle sağa kaydırarak (bitwise right shift)
		 * değiştirir ve bu yeni değeri mevcut "hash1Value" değerine bitwise XOR işlemi
		 * uygular.
		 */
		hash1Value ^= hash1Value >>> 16;
		// Sabit çarpma işlemi hash değerinin daha da dağılması için kullanılır.*/
		hash1Value *= 0x186A0; // ondalık karşılığı: 100000
		hash1Value ^= hash1Value >>> 13;
		hash1Value *= 0x9999B; // ondalık karşılığı: 626491
		hash1Value ^= hash1Value >>> 16; // Son olarak, hash değeri tekrar sağa kaydırılır ve döndürülür.

		int hash1result = bitwiseMod11(hash1Value); // fonksiyon sonucunun 11 ile modunu alırırz ki binary arrayin
													// kapasitesini aşmasın.
		return hash1result;
	}

	public static int hash2(int intFruit) {

		int hash2Value = 0; // hash değişkeni başlangıçta 0

		for (int i = 0; i < 4; i++) { // int 4 byte olduğu için 4 kez döngüye giriyoruz

			/*
			 * 5 bit sola kaydırılır ve en soldaki 5 bit 0 ile doldurur Ardından kendisiyle
			 * sağa kaydırılır ve en sağdaki 5 bit 0 ile doldurur. Daha sonra bu iki işleme
			 * bitwise OR uygular.
			 */
			hash2Value = (hash2Value << 5) | (hash2Value >>> 27);
			hash2Value ^= (intFruit & 0xFF); // least significant byte (en sağdaki 8 bit) ile XOR yapılır
			intFruit >>>= 8; // 8 bit sağa kaydır ve ve bir sonraki byte için hazır hale getir.
		}

		int hash2result = bitwiseMod11(hash2Value); // sonucun 11 ile modunu al
		return hash2result;
	}

	public static int hash3(int intFruit) {

		int hash3Value = intFruit; // girdinin kendisini başlangıç değeri olarak atar.

		// sağa kaydırma, çarpma ve XOR işlemleri yaparak hash değerini değiştirir.
		hash3Value ^= hash3Value >>> 16;
		// daha iyi dağılım için sabitler ile çarptık.
		hash3Value *= 0x3E8D0; // ondalık karşılığı: 259280
		hash3Value ^= hash3Value >>> 13;
		hash3Value *= 0x1E240; // ondalık karşılığı: 12313
		hash3Value ^= hash3Value >>> 16;

		/*
		 * "hash3Value" değişkeninin değeri önce sola doğru 9 bit kaydırılır, ardından
		 * bitwise complement uygulanır ve bu değer "hash3Value" değişkenine eklenir.
		 */
		hash3Value += ~(hash3Value << 9); // Bitwise complement (~): 1 olan bitler 0 yapılır ve 0 olan bitler 1 yapılır.
		/*
		 * Değişkeninin değeri sağa doğru 14 bit kaydırılır sonra bitwise OR işlemi
		 * kullanılarak kendisi ve sola doğru 18 bit kaydırılarak elde edilen değer
		 * arasında birleştirilir. Daha sonra, bitwise XOR işlemi uygulanarak
		 * "hash3Value" değişkeninin değeri değiştirilir.
		 */
		hash3Value ^= (hash3Value >>> 14) | (hash3Value << 18);
		hash3Value += ~(hash3Value << 21);
		hash3Value ^= (hash3Value >>> 10) | (hash3Value << 22);

		int hash3result = bitwiseMod11(hash3Value); // sonucun 11 ile modunu al
		return hash3result;
	}

	public static void checkArray(byte[] bloomFilter) { // kontrol
		System.out.println("There are no fruits chosen now.");

		for (int i = 0; i < bloomFilter.length; i++) {
			bloomFilter[i] = 0b00000000;
			System.out.print("Byte " + i + " : ");
			for (int j = 7; j >= 0; j--) {
				int bit = (bloomFilter[i] >> j) & 1;
				System.out.print(bit);
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {

		byte[] bloomFilter = new byte[2]; // 2 byte'lık bitwise array yani 16 bit'lik değer tutacak!!!!!!

		checkArray(bloomFilter); // seçim yapılmamış arrayı göstermek için

		JFrame frame = new JFrame("PICK FRUITS YOU LIKE");

		JButton apple = new JButton("Apple");
		apple.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color apperu = new Color(204, 6, 0);
				apple.setBackground(apperu);
				int appleValue = 1;

				System.out.println("hash 1: " + hash1(appleValue)); // kontrol satırı
				int bitIndex1 = hash1(appleValue) % 8; // kaçıcı bit'teyim

				if (hash1(appleValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex1);
				} else {
					bloomFilter[0] |= (1 << bitIndex1); // ilk byte'ta işlem yapar
				}

				System.out.println("hash 2: " + hash2(appleValue));
				int bitIndex2 = hash2(appleValue) % 8; // kaçıcı bit'teyim

				if (hash2(appleValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex2);
				} else {
					bloomFilter[0] |= (1 << bitIndex2); // ilk byte'ta işlem yapar
				}

				System.out.println("hash 3: " + hash3(appleValue));
				int bitIndex3 = hash3(appleValue) % 8; // kaçıcı bit'teyim

				if (hash3(appleValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex3);
				} else {
					bloomFilter[0] |= (1 << bitIndex3); // ilk byte'ta işlem yapar
				}

				System.out.println("Apple is chosen."); // kontrol

				for (int i = 0; i < bloomFilter.length; i++) {
					System.out.print("Byte " + i + " : ");
					for (int j = 7; j >= 0; j--) {
						int bit = (bloomFilter[i] >> j) & 1;
						System.out.print(bit);
					}
					System.out.println();
				}
			}
		});

		JButton banana = new JButton("Banana");
		banana.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color bananna = new Color(255, 255, 53);
				banana.setBackground(bananna);
				int bananaValue = 2;

				System.out.println("hash 1: " + hash1(bananaValue)); // kontrol satırı
				int bitIndex1 = hash1(bananaValue) % 8; // kaçıcı bit'teyim

				if (hash1(bananaValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex1);
				} else {
					bloomFilter[0] |= (1 << bitIndex1); // ilk byte'ta işlem yapar
				}

				System.out.println("hash 2: " + hash2(bananaValue));
				int bitIndex2 = hash2(bananaValue) % 8; // kaçıcı bit'teyim

				if (hash2(bananaValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex2);
				} else {
					bloomFilter[0] |= (1 << bitIndex2); // ilk byte'ta işlem yapar
				}

				System.out.println("hash 3: " + hash3(bananaValue));
				int bitIndex3 = hash3(bananaValue) % 8; // kaçıcı bit'teyim

				if (hash3(bananaValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex3);
				} else {
					bloomFilter[0] |= (1 << bitIndex3); // ilk byte'ta işlem yapar
				}

				System.out.println("Banana is chosen."); // kontrol

				for (int i = 0; i < bloomFilter.length; i++) {
					System.out.print("Byte " + i + " : ");
					for (int j = 7; j >= 0; j--) {
						int bit = (bloomFilter[i] >> j) & 1;
						System.out.print(bit);
					}
					System.out.println();
				}
			}
		});

		JButton kiwi = new JButton("Kiwi");
		kiwi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color kkkiwi = new Color(122, 150, 15);
				kiwi.setBackground(kkkiwi);
				int kiwiValue = 3;

				System.out.println("hash 1: " + hash1(kiwiValue)); // kontrol satırı
				int bitIndex1 = hash1(kiwiValue) % 8; // kaçıcı bit'teyim

				if (hash1(kiwiValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex1);
				} else {
					bloomFilter[0] |= (1 << bitIndex1); // ilk byte'ta işlem yapar
				}

				System.out.println("hash 2: " + hash2(kiwiValue));
				int bitIndex2 = hash2(kiwiValue) % 8; // kaçıcı bit'teyim

				if (hash2(kiwiValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex2);
				} else {
					bloomFilter[0] |= (1 << bitIndex2); // ilk byte'ta işlem yapar
				}

				System.out.println("hash 3: " + hash3(kiwiValue));
				int bitIndex3 = hash3(kiwiValue) % 8; // kaçıcı bit'teyim

				if (hash3(kiwiValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex3);
				} else {
					bloomFilter[0] |= (1 << bitIndex3); // ilk byte'ta işlem yapar
				}

				System.out.println("Kiwi is chosen."); // kontrol

				for (int i = 0; i < bloomFilter.length; i++) {
					System.out.print("Byte " + i + " : ");
					for (int j = 7; j >= 0; j--) {
						int bit = (bloomFilter[i] >> j) & 1;
						System.out.print(bit);
					}
					System.out.println();
				}
			}
		});

		JButton grape = new JButton("Grape");
		grape.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color grappe = new Color(66, 28, 82);
				grape.setBackground(grappe);
				grape.setForeground(Color.WHITE);
				int grapeValue = 4;

				System.out.println("hash 1: " + hash1(grapeValue)); // kontrol satırı
				int bitIndex1 = hash1(grapeValue) % 8; // kaçıcı bit'teyim

				if (hash1(grapeValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex1);
				} else {
					bloomFilter[0] |= (1 << bitIndex1); // ilk byte'ta işlem yapar
				}

				System.out.println("hash 2: " + hash2(grapeValue));
				int bitIndex2 = hash2(grapeValue) % 8; // kaçıcı bit'teyim

				if (hash2(grapeValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex2);
				} else {
					bloomFilter[0] |= (1 << bitIndex2); // ilk byte'ta işlem yapar
				}

				System.out.println("hash 3: " + hash3(grapeValue));
				int bitIndex3 = hash3(grapeValue) % 8; // kaçıcı bit'teyim

				if (hash3(grapeValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex3);
				} else {
					bloomFilter[0] |= (1 << bitIndex3); // ilk byte'ta işlem yapar
				}

				System.out.println("Grape is chosen."); // kontrol

				for (int i = 0; i < bloomFilter.length; i++) {
					System.out.print("Byte " + i + " : ");
					for (int j = 7; j >= 0; j--) {
						int bit = (bloomFilter[i] >> j) & 1;
						System.out.print(bit);
					}
					System.out.println();
				}
			}
		});

		JButton watermelon = new JButton("Watermelon");
		watermelon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color watamero = new Color(210, 59, 104);
				watermelon.setBackground(watamero);
				int watermelonValue = 5;

				System.out.println("hash 1: " + hash1(watermelonValue)); // kontrol satırı
				int bitIndex1 = hash1(watermelonValue) % 8; // kaçıcı bit'teyim

				if (hash1(watermelonValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex1);
				} else {
					bloomFilter[0] |= (1 << bitIndex1); // ilk byte'ta işlem yapar
				}

				System.out.println("hash 2: " + hash2(watermelonValue));
				int bitIndex2 = hash2(watermelonValue) % 8; // kaçıcı bit'teyim

				if (hash2(watermelonValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex2);
				} else {
					bloomFilter[0] |= (1 << bitIndex2); // ilk byte'ta işlem yapar
				}

				System.out.println("hash 3: " + hash3(watermelonValue));
				int bitIndex3 = hash3(watermelonValue) % 8; // kaçıcı bit'teyim

				if (hash3(watermelonValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex3);
				} else {
					bloomFilter[0] |= (1 << bitIndex3); // ilk byte'ta işlem yapar
				}

				System.out.println("Watermelon is chosen."); // kontrol

				for (int i = 0; i < bloomFilter.length; i++) {
					System.out.print("Byte " + i + " : ");
					for (int j = 7; j >= 0; j--) {
						int bit = (bloomFilter[i] >> j) & 1;
						System.out.print(bit);
					}
					System.out.println();
				}
			}
		});

		JButton strawberry = new JButton("Strawberry");
		strawberry.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color sbrry = new Color(200, 63, 73);
				strawberry.setBackground(sbrry);
				int strawberryValue = 6;

				System.out.println("hash 1: " + hash1(strawberryValue)); // kontrol satırı
				int bitIndex1 = hash1(strawberryValue) % 8; // kaçıcı bit'teyim

				if (hash1(strawberryValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex1);
				} else {
					bloomFilter[0] |= (1 << bitIndex1); // ilk byte'ta işlem yapar
				}

				System.out.println("hash 2: " + hash2(strawberryValue));
				int bitIndex2 = hash2(strawberryValue) % 8; // kaçıcı bit'teyim

				if (hash2(strawberryValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex2);
				} else {
					bloomFilter[0] |= (1 << bitIndex2); // ilk byte'ta işlem yapar
				}

				System.out.println("hash 3: " + hash3(strawberryValue));
				int bitIndex3 = hash3(strawberryValue) % 8; // kaçıcı bit'teyim

				if (hash3(strawberryValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex3);
				} else {
					bloomFilter[0] |= (1 << bitIndex3); // ilk byte'ta işlem yapar
				}

				System.out.println("Strawberry is chosen."); // kontrol

				for (int i = 0; i < bloomFilter.length; i++) {
					System.out.print("Byte " + i + " : ");
					for (int j = 7; j >= 0; j--) {
						int bit = (bloomFilter[i] >> j) & 1;
						System.out.print(bit);
					}
					System.out.println();
				}

			}
		});

		JButton orange = new JButton("Orange");
		orange.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color orng = new Color(255, 168, 54);
				orange.setBackground(orng);
				int orangeValue = 7;

				System.out.println("hash 1: " + hash1(orangeValue)); // kontrol satırı
				int bitIndex1 = hash1(orangeValue) % 8; // kaçıcı bit'teyim

				if (hash1(orangeValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex1);
				} else {
					bloomFilter[0] |= (1 << bitIndex1); // ilk byte'ta işlem yapar
				}

				System.out.println("hash 2: " + hash2(orangeValue));
				int bitIndex2 = hash2(orangeValue) % 8; // kaçıcı bit'teyim

				if (hash2(orangeValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex2);
				} else {
					bloomFilter[0] |= (1 << bitIndex2); // ilk byte'ta işlem yapar
				}

				System.out.println("hash 3: " + hash3(orangeValue));
				int bitIndex3 = hash3(orangeValue) % 8; // kaçıcı bit'teyim

				if (hash3(orangeValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex3);
				} else {
					bloomFilter[0] |= (1 << bitIndex3); // ilk byte'ta işlem yapar
				}

				System.out.println("Orange is chosen."); // kontrol

				for (int i = 0; i < bloomFilter.length; i++) {
					System.out.print("Byte " + i + " : ");
					for (int j = 7; j >= 0; j--) {
						int bit = (bloomFilter[i] >> j) & 1;
						System.out.print(bit);
					}
					System.out.println();
				}
			}
		});

		JButton cherry = new JButton("Cherry");
		cherry.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color chry = new Color(255, 87, 51);
				cherry.setBackground(chry);
				int cherryValue = 8;

				System.out.println("hash 1: " + hash1(cherryValue)); // kontrol satırı
				int bitIndex1 = hash1(cherryValue) % 8; // kaçıcı bit'teyim

				if (hash1(cherryValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex1);
				} else {
					bloomFilter[0] |= (1 << bitIndex1); // ilk byte'ta işlem yapar
				}

				System.out.println("hash 2: " + hash2(cherryValue));
				int bitIndex2 = hash2(cherryValue) % 8; // kaçıcı bit'teyim

				if (hash2(cherryValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex2);
				} else {
					bloomFilter[0] |= (1 << bitIndex2); // ilk byte'ta işlem yapar
				}

				System.out.println("hash 3: " + hash3(cherryValue));
				int bitIndex3 = hash3(cherryValue) % 8; // kaçıcı bit'teyim

				if (hash3(cherryValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex3);
				} else {
					bloomFilter[0] |= (1 << bitIndex3); // ilk byte'ta işlem yapar
				}

				System.out.println("Cherry is chosen."); // kontrol

				for (int i = 0; i < bloomFilter.length; i++) {
					System.out.print("Byte " + i + " : ");
					for (int j = 7; j >= 0; j--) {
						int bit = (bloomFilter[i] >> j) & 1;
						System.out.print(bit);
					}
					System.out.println();
				}

			}
		});

		JButton peach = new JButton("Peach");
		peach.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color phc = new Color(255, 229, 180);
				peach.setBackground(phc);
				int peachValue = 9;

				System.out.println("hash 1: " + hash1(peachValue)); // kontrol satırı
				int bitIndex1 = hash1(peachValue) % 8; // kaçıcı bit'teyim

				if (hash1(peachValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex1);
				} else {
					bloomFilter[0] |= (1 << bitIndex1); // ilk byte'ta işlem yapar
				}

				System.out.println("hash 2: " + hash2(peachValue));
				int bitIndex2 = hash2(peachValue) % 8; // kaçıcı bit'teyim

				if (hash2(peachValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex2);
				} else {
					bloomFilter[0] |= (1 << bitIndex2); // ilk byte'ta işlem yapar
				}

				System.out.println("hash 3: " + hash3(peachValue));
				int bitIndex3 = hash3(peachValue) % 8; // kaçıcı bit'teyim

				if (hash3(peachValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex3);
				} else {
					bloomFilter[0] |= (1 << bitIndex3); // ilk byte'ta işlem yapar
				}

				for (int i = 0; i < bloomFilter.length; i++) {
					System.out.print("Byte " + i + " : ");
					for (int j = 7; j >= 0; j--) {
						int bit = (bloomFilter[i] >> j) & 1;
						System.out.print(bit);
					}
					System.out.println();
				}

			}
		});

		JButton pineapple = new JButton("Pineapple");
		pineapple.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color papple = new Color(254, 234, 99);
				pineapple.setBackground(papple);
				int pineappleValue = 10;

				System.out.println("hash 1: " + hash1(pineappleValue)); // kontrol satırı
				int bitIndex1 = hash1(pineappleValue) % 8; // kaçıcı bit'teyim

				if (hash1(pineappleValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex1);
				} else {
					bloomFilter[0] |= (1 << bitIndex1); // ilk byte'ta işlem yapar
				}

				System.out.println("hash 2: " + hash2(pineappleValue));
				int bitIndex2 = hash2(pineappleValue) % 8; // kaçıcı bit'teyim

				if (hash2(pineappleValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex2);
				} else {
					bloomFilter[0] |= (1 << bitIndex2); // ilk byte'ta işlem yapar
				}

				System.out.println("hash 3: " + hash3(pineappleValue));
				int bitIndex3 = hash3(pineappleValue) % 8; // kaçıcı bit'teyim

				if (hash3(pineappleValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex3);
				} else {
					bloomFilter[0] |= (1 << bitIndex3); // ilk byte'ta işlem yapar
				}

				System.out.println("Pineapple is chosen."); // kontrol

				for (int i = 0; i < bloomFilter.length; i++) {
					System.out.print("Byte " + i + " : ");
					for (int j = 7; j >= 0; j--) {
						int bit = (bloomFilter[i] >> j) & 1;
						System.out.print(bit);
					}
					System.out.println();
				}

			}
		});

		JButton melon = new JButton("Melon");
		melon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color meron = new Color(254, 186, 173);
				melon.setBackground(meron);
				int melonValue = 11;

				System.out.println("hash 1: " + hash1(melonValue)); // kontrol satırı
				int bitIndex1 = hash1(melonValue) % 8; // kaçıcı bit'teyim

				if (hash1(melonValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex1);
				} else {
					bloomFilter[0] |= (1 << bitIndex1); // ilk byte'ta işlem yapar
				}

				System.out.println("hash 2: " + hash2(melonValue));
				int bitIndex2 = hash2(melonValue) % 8; // kaçıcı bit'teyim

				if (hash2(melonValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex2);
				} else {
					bloomFilter[0] |= (1 << bitIndex2); // ilk byte'ta işlem yapar
				}

				System.out.println("hash 3: " + hash3(melonValue));
				int bitIndex3 = hash3(melonValue) % 8; // kaçıcı bit'teyim

				if (hash3(melonValue) > 7) { // 2. byte değerlerini görmek için
					bloomFilter[1] |= (1 << bitIndex3);
				} else {
					bloomFilter[0] |= (1 << bitIndex3); // ilk byte'ta işlem yapar
				}

				System.out.println("Melon is chosen."); // kontrol

				for (int i = 0; i < bloomFilter.length; i++) {
					System.out.print("Byte " + i + " : ");
					for (int j = 7; j >= 0; j--) {
						int bit = (bloomFilter[i] >> j) & 1;
						System.out.print(bit);
					}
					System.out.println();
				}
			}
		});

		JButton reset = new JButton("RESET");
		reset.setBounds(250, 300, 100, 50); // x, y, genişlik, yükseklik
		frame.add(reset);
		Map<JButton, Color> previousColors = new HashMap<>();
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkArray(bloomFilter);

				for (JButton button : previousColors.keySet()) {
					button.setBackground(previousColors.get(button));
				}
				previousColors.clear();
				if (!apple.getBackground().equals(null)) {
					previousColors.put(apple, apple.getBackground());
					apple.setBackground(null);
					apple.setForeground(Color.BLACK);
				}
				if (!banana.getBackground().equals(null)) {
					previousColors.put(banana, banana.getBackground());
					banana.setBackground(null);
					banana.setForeground(Color.BLACK);
				}
				if (!kiwi.getBackground().equals(null)) {
					previousColors.put(kiwi, kiwi.getBackground());
					kiwi.setBackground(null);
					kiwi.setForeground(Color.BLACK);
				}
				if (!grape.getBackground().equals(null)) {
					previousColors.put(grape, grape.getBackground());
					grape.setBackground(null);
					grape.setForeground(Color.BLACK);
				}
				if (!watermelon.getBackground().equals(null)) {
					previousColors.put(watermelon, watermelon.getBackground());
					watermelon.setBackground(null);
					watermelon.setForeground(Color.BLACK);
				}
				if (!strawberry.getBackground().equals(null)) {
					previousColors.put(strawberry, strawberry.getBackground());
					strawberry.setBackground(null);
					strawberry.setForeground(Color.BLACK);
				}

				if (!orange.getBackground().equals(null)) {
					previousColors.put(orange, orange.getBackground());
					orange.setBackground(null);
					orange.setForeground(Color.BLACK);
				}
				if (!cherry.getBackground().equals(null)) {
					previousColors.put(cherry, cherry.getBackground());
					cherry.setBackground(null);
					cherry.setForeground(Color.BLACK);
				}
				if (!peach.getBackground().equals(null)) {
					previousColors.put(peach, peach.getBackground());
					peach.setBackground(null);
					peach.setForeground(Color.BLACK);
				}

				if (!pineapple.getBackground().equals(null)) {
					previousColors.put(pineapple, pineapple.getBackground());
					pineapple.setBackground(null);
					pineapple.setForeground(Color.BLACK);
				}
				if (!melon.getBackground().equals(null)) {
					previousColors.put(melon, melon.getBackground());
					melon.setBackground(null);
					melon.setForeground(Color.BLACK);
				}
			}
		});
		Font newFont = new Font("Times New Roman", Font.BOLD, 13);

		JButton[] allButtons = { apple, banana, kiwi, grape, watermelon, strawberry, orange, cherry, peach, pineapple, melon };

		JButton PLS = new JButton("Pick whatever");
		PLS.setFont(newFont);
		Color oppa = new Color(183, 107, 181);
		PLS.setForeground(oppa);
		{
			PLS.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Random rand = new Random();
					int randomButton = rand.nextInt(11);
					allButtons[randomButton].doClick();

					int red = (int) (Math.random() * 256);
					int green = (int) (Math.random() * 256);
					int blue = (int) (Math.random() * 256);
					int revred = 255 - red;
					int revgreen = 255 - green;
					int revblue = 255 - blue;
					Color revRandomColor = new Color(revred, revgreen, revblue);
					Color randomColor = new Color(red, green, blue);
					PLS.setBackground(randomColor);
					PLS.setForeground(revRandomColor);
				}
			});
		}

		JButton check = new JButton("CHECK");
		check.setBounds(50, 300, 100, 50); // x, y, genişlik, yükseklik
		frame.add(check);
		check.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// asking user for the fruit to be checked
				String fruit = JOptionPane.showInputDialog(null, "Enter the fruit to check:", "Fruit Checker",
						JOptionPane.INFORMATION_MESSAGE);

				fruit = fruit.toLowerCase(); // inputun küçük harfle başlamasını sağlmak için

				boolean hasNumbers = fruit.matches(".*\\d+.*"); // sayı yazınca eror veriyor

				switch (fruit) {
				case "apple":
					fruit = "1";
					break;
				case "banana":
					fruit = "2";
					break;
				case "kiwi":
					fruit = "3";
					break;
				case "grape":
					fruit = "4";
					break;
				case "watermelon":
					fruit = "5";
					break;
				case "strawberry":
					fruit = "6";
					break;
				case "orange":
					fruit = "7";
					break;
				case "cherry":
					fruit = "8";
					break;
				case "peach":
					fruit = "9";
					break;
				case "pineapple":
					fruit = "10";
					break;
				case "melon":
					fruit = "11";
					break;
				}

				if (fruit != null && !fruit.isEmpty() && !hasNumbers
						&& (fruit.equals("1") || fruit.equals("2") || fruit.equals("3") || fruit.equals("4")
								|| fruit.equals("5") || fruit.equals("6") || fruit.equals("7") || fruit.equals("8")
								|| fruit.equals("9") || fruit.equals("10") || fruit.equals("11"))) { // BURA EKLENDI
					System.out.println("You selected: " + fruit); // kontrol satırı
					int intFruit = Integer.parseInt(fruit);

					System.out.println("hash 1: " + hash1(intFruit));
					System.out.println("hash 2: " + hash2(intFruit));
					System.out.println("hash 3: " + hash3(intFruit));

					if (checkHash1(intFruit) == true && checkHash2(intFruit) == true && checkHash3(intFruit) == true
							&& hasNumbers == false) {
						// found
						JOptionPane.showMessageDialog(null, "This fruit might have been selected.", "Fruit Checker",
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						// not found
						JOptionPane.showMessageDialog(null, "This fruit is definitely not selected.", "Fruit Checker",
								JOptionPane.INFORMATION_MESSAGE);
					}
				} else {
					// error message if there is no input or if it has int in it.
					JOptionPane.showMessageDialog(null, "You typed wrong. Please enter a fruit to check.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}

			public boolean checkHash1(int intFruit) {
				int bitIndex1 = hash1(intFruit) % 8;

				if (hash1(intFruit) > 7) {
					return (bloomFilter[1] & (1 << bitIndex1)) != 0;

				} else {
					return (bloomFilter[0] & (1 << bitIndex1)) != 0;
				}
			}

			public boolean checkHash2(int intFruit) {
				int bitIndex2 = hash2(intFruit) % 8;

				if (hash2(intFruit) > 7) {
					return (bloomFilter[1] & (1 << bitIndex2)) != 0;
				} else {
					return (bloomFilter[0] & (1 << bitIndex2)) != 0;
				}
			}

			public boolean checkHash3(int intFruit) {
				int bitIndex3 = hash3(intFruit) % 8;

				if (hash3(intFruit) > 7) {
					return (bloomFilter[1] & (1 << bitIndex3)) != 0;
				} else {
					return (bloomFilter[0] & (1 << bitIndex3)) != 0;
				}
			}

		});

		JPanel mainPanel = new JPanel(new GridLayout(5, 2)); // panel with 5 row and 2 columns
		mainPanel.add(apple);
		mainPanel.add(banana);
		mainPanel.add(kiwi);
		mainPanel.add(grape);
		mainPanel.add(watermelon);
		mainPanel.add(strawberry);
		mainPanel.add(orange);
		mainPanel.add(cherry);
		mainPanel.add(peach);
		mainPanel.add(pineapple);
		mainPanel.add(melon);
		mainPanel.add(PLS);

		frame.getContentPane().add(mainPanel);
		frame.pack();
		frame.setVisible(true);
		frame.setSize(400, 400);
		frame.setLocation(300, 100);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JFrame frame2 = new JFrame("Output");
		JTextArea textArea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(textArea);
		frame2.add(scrollPane);
		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame2.setSize(300, 400);
		frame2.setLocation(750, 100);
		frame2.setVisible(true);

		textArea.append("There are no fruits chosen now.\n");
		for (int i = 0; i < bloomFilter.length; i++) {
			textArea.append("Byte " + i + " : ");
			for (int j = 7; j >= 0; j--) {
				int bit = (bloomFilter[i] >> j) & 1;
				textArea.append("" + bit);
			}
			textArea.append("\n");
		}
		textArea.setCaretPosition(textArea.getDocument().getLength());

		apple.addActionListener(e -> {

			textArea.append("Apple is chosen.\n");
			textArea.append("hash 1: " + hash1(1) + "\n");
			int bitIndex1 = hash1(1) % 8; // kaçıcı bit'teyim

			if (hash1(1) > 7) { // 2. byte değerlerini görmek için
				bloomFilter[1] |= (1 << bitIndex1);
			} else {
				bloomFilter[0] |= (1 << bitIndex1); // ilk byte'ta işlem yapar
			}

			textArea.append("hash 2: " + hash2(1) + "\n");
			int bitIndex2 = hash2(1) % 8; // kaçıcı bit'teyim

			if (hash2(1) > 7) { // 2. byte değerlerini görmek için
				bloomFilter[1] |= (1 << bitIndex2);
			} else {
				bloomFilter[0] |= (1 << bitIndex2); // ilk byte'ta işlem yapar
			}

			textArea.append("hash 3: " + hash3(1) + "\n");
			int bitIndex3 = hash3(1) % 8; // kaçıcı bit'teyim

			if (hash3(1) > 7) { // 2. byte değerlerini görmek için
				bloomFilter[1] |= (1 << bitIndex3);
			} else {
				bloomFilter[0] |= (1 << bitIndex3); // ilk byte'ta işlem yapar
			}

			for (int i = 0; i < bloomFilter.length; i++) {
				textArea.append("Byte " + i + " : ");
				for (int j = 7; j >= 0; j--) {
					int bit = (bloomFilter[i] >> j) & 1;
					textArea.append("" + bit);
				}
				textArea.append("\n");
			}
			textArea.setCaretPosition(textArea.getDocument().getLength());
		});

		banana.addActionListener(e -> {
			textArea.append("Banana is chosen.\n");
			textArea.append("hash 1: " + hash1(2) + "\n");
			int bitIndex1 = hash1(2) % 8; // kaçıcı bit'teyim

			if (hash1(2) > 7) { // 2. byte değerlerini görmek için
				bloomFilter[1] |= (1 << bitIndex1);
			} else {
				bloomFilter[0] |= (1 << bitIndex1); // ilk byte'ta işlem yapar
			}

			textArea.append("hash 2: " + hash2(2) + "\n");
			int bitIndex2 = hash2(2) % 8; // kaçıcı bit'teyim

			if (hash2(2) > 7) { // 2. byte değerlerini görmek için
				bloomFilter[1] |= (1 << bitIndex2);
			} else {
				bloomFilter[0] |= (1 << bitIndex2); // ilk byte'ta işlem yapar
			}

			textArea.append("hash 3: " + hash3(2) + "\n");
			int bitIndex3 = hash3(2) % 8; // kaçıcı bit'teyim

			if (hash3(2) > 7) { // 2. byte değerlerini görmek için
				bloomFilter[1] |= (1 << bitIndex3);
			} else {
				bloomFilter[0] |= (1 << bitIndex3); // ilk byte'ta işlem yapar
			}

			for (int i = 0; i < bloomFilter.length; i++) {
				textArea.append("Byte " + i + " : ");
				for (int j = 7; j >= 0; j--) {
					int bit = (bloomFilter[i] >> j) & 1;
					textArea.append("" + bit);
				}
				textArea.append("\n");
			}
			textArea.setCaretPosition(textArea.getDocument().getLength());
		});

		kiwi.addActionListener(e -> {
			textArea.append("Kiwi is chosen.\n");
			   textArea.append("hash 1: "+hash1(3)+"\n");	
			   int bitIndex1 = hash1(3)% 8; // kaçıcı bit'teyim
	           
	           if (hash1(3) > 7) { // 2. byte değerlerini görmek için
	           	bloomFilter[1] |= (1 << bitIndex1);         	
	           } else {
	           	bloomFilter[0] |= (1 << bitIndex1);  // ilk byte'ta işlem yapar
	           } 
			   
			   textArea.append("hash 2: "+hash2(3)+"\n");
			   int bitIndex2 = hash2(3)% 8; // kaçıcı bit'teyim
	           
	           if (hash2(3) > 7) { // 2. byte değerlerini görmek için
	           	bloomFilter[1] |= (1 << bitIndex2);         	
	           } else {
	           	bloomFilter[0] |= (1 << bitIndex2);  // ilk byte'ta işlem yapar
	           }
	    	 		   
			   textArea.append("hash 3: "+hash3(3)+"\n");	   
			   int bitIndex3 = hash3(3)% 8; // kaçıcı bit'teyim
	            
	           if (hash3(3) > 7) { // 2. byte değerlerini görmek için
	            bloomFilter[1] |= (1 << bitIndex3);         	
	           } else {
	            bloomFilter[0] |= (1 << bitIndex3);  // ilk byte'ta işlem yapar
	           }
			   
			   for (int i = 0; i < bloomFilter.length; i++) {
				textArea.append("Byte " + i + " : ");
				for (int j = 7; j >= 0; j--) {
					int bit = (bloomFilter[i] >> j) & 1;
					textArea.append(""+bit);
				}
				textArea.append("\n");
			}
			   textArea.setCaretPosition(textArea.getDocument().getLength());
			});

		grape.addActionListener(e -> {
			textArea.append("Grape is chosen.\n");
			   textArea.append("hash 1: "+hash1(4)+"\n");	
			   int bitIndex1 = hash1(4)% 8; // kaçıcı bit'teyim
	           
	           if (hash1(4) > 7) { // 2. byte değerlerini görmek için
	           	bloomFilter[1] |= (1 << bitIndex1);         	
	           } else {
	           	bloomFilter[0] |= (1 << bitIndex1);  // ilk byte'ta işlem yapar
	           } 
			   
			   textArea.append("hash 2: "+hash2(4)+"\n");
			   int bitIndex2 = hash2(4)% 8; // kaçıcı bit'teyim
	           
	           if (hash2(4) > 7) { // 2. byte değerlerini görmek için
	           	bloomFilter[1] |= (1 << bitIndex2);         	
	           } else {
	           	bloomFilter[0] |= (1 << bitIndex2);  // ilk byte'ta işlem yapar
	           }
	    	 		   
			   textArea.append("hash 3: "+hash3(4)+"\n");	   
			   int bitIndex3 = hash3(4)% 8; // kaçıcı bit'teyim
	            
	           if (hash3(4) > 7) { // 2. byte değerlerini görmek için
	            bloomFilter[1] |= (1 << bitIndex3);         	
	           } else {
	            bloomFilter[0] |= (1 << bitIndex3);  // ilk byte'ta işlem yapar
	           }
			   
			   for (int i = 0; i < bloomFilter.length; i++) {
				textArea.append("Byte " + i + " : ");
				for (int j = 7; j >= 0; j--) {
					int bit = (bloomFilter[i] >> j) & 1;
					textArea.append(""+bit);
				}
				textArea.append("\n");
			}
			   textArea.setCaretPosition(textArea.getDocument().getLength());
			});
		
		watermelon.addActionListener(e -> {
			textArea.append("Watermelon is chosen.\n");
			   textArea.append("hash 1: "+hash1(5)+"\n");	
			   int bitIndex1 = hash1(5)% 8; // kaçıcı bit'teyim
	           
	           if (hash1(5) > 7) { // 2. byte değerlerini görmek için
	           	bloomFilter[1] |= (1 << bitIndex1);         	
	           } else {
	           	bloomFilter[0] |= (1 << bitIndex1);  // ilk byte'ta işlem yapar
	           } 
			   
			   textArea.append("hash 2: "+hash2(5)+"\n");
			   int bitIndex2 = hash2(5)% 8; // kaçıcı bit'teyim
	           
	           if (hash2(5) > 7) { // 2. byte değerlerini görmek için
	           	bloomFilter[1] |= (1 << bitIndex2);         	
	           } else {
	           	bloomFilter[0] |= (1 << bitIndex2);  // ilk byte'ta işlem yapar
	           }
	    	 		   
			   textArea.append("hash 3: "+hash3(5)+"\n");	   
			   int bitIndex3 = hash3(5)% 8; // kaçıcı bit'teyim
	            
	           if (hash3(5) > 7) { // 2. byte değerlerini görmek için
	            bloomFilter[1] |= (1 << bitIndex3);         	
	           } else {
	            bloomFilter[0] |= (1 << bitIndex3);  // ilk byte'ta işlem yapar
	           }
			   
			   for (int i = 0; i < bloomFilter.length; i++) {
				textArea.append("Byte " + i + " : ");
				for (int j = 7; j >= 0; j--) {
					int bit = (bloomFilter[i] >> j) & 1;
					textArea.append(""+bit);
				}
				textArea.append("\n");
			}
			   textArea.setCaretPosition(textArea.getDocument().getLength());
			});

		strawberry.addActionListener(e -> {
			textArea.append("Strawberry is chosen.\n");
			   textArea.append("hash 1: "+hash1(6)+"\n");	
			   int bitIndex1 = hash1(6)% 8; // kaçıcı bit'teyim
	           
	           if (hash1(6) > 7) { // 2. byte değerlerini görmek için
	           	bloomFilter[1] |= (1 << bitIndex1);         	
	           } else {
	           	bloomFilter[0] |= (1 << bitIndex1);  // ilk byte'ta işlem yapar
	           } 
			   
			   textArea.append("hash 2: "+hash2(6)+"\n");
			   int bitIndex2 = hash2(6)% 8; // kaçıcı bit'teyim
	           
	           if (hash2(6) > 7) { // 2. byte değerlerini görmek için
	           	bloomFilter[1] |= (1 << bitIndex2);         	
	           } else {
	           	bloomFilter[0] |= (1 << bitIndex2);  // ilk byte'ta işlem yapar
	           }
	    	 		   
			   textArea.append("hash 3: "+hash3(6)+"\n");	   
			   int bitIndex3 = hash3(6)% 8; // kaçıcı bit'teyim
	            
	           if (hash3(6) > 7) { // 2. byte değerlerini görmek için
	            bloomFilter[1] |= (1 << bitIndex3);         	
	           } else {
	            bloomFilter[0] |= (1 << bitIndex3);  // ilk byte'ta işlem yapar
	           }
			   
			   for (int i = 0; i < bloomFilter.length; i++) {
				textArea.append("Byte " + i + " : ");
				for (int j = 7; j >= 0; j--) {
					int bit = (bloomFilter[i] >> j) & 1;
					textArea.append(""+bit);
				}
				textArea.append("\n");
			}
			   textArea.setCaretPosition(textArea.getDocument().getLength());
			});
		
		orange.addActionListener(e -> {
			textArea.append("Orange is chosen.\n");
			   textArea.append("hash 1: "+hash1(7)+"\n");	
			   int bitIndex1 = hash1(7)% 8; // kaçıcı bit'teyim
	           
	           if (hash1(7) > 7) { // 2. byte değerlerini görmek için
	           	bloomFilter[1] |= (1 << bitIndex1);         	
	           } else {
	           	bloomFilter[0] |= (1 << bitIndex1);  // ilk byte'ta işlem yapar
	           } 
			   
			   textArea.append("hash 2: "+hash2(7)+"\n");
			   int bitIndex2 = hash2(7)% 8; // kaçıcı bit'teyim
	           
	           if (hash2(7) > 7) { // 2. byte değerlerini görmek için
	           	bloomFilter[1] |= (1 << bitIndex2);         	
	           } else {
	           	bloomFilter[0] |= (1 << bitIndex2);  // ilk byte'ta işlem yapar
	           }
	    	 		   
			   textArea.append("hash 3: "+hash3(7)+"\n");	   
			   int bitIndex3 = hash3(7)% 8; // kaçıcı bit'teyim
	            
	           if (hash3(7) > 7) { // 2. byte değerlerini görmek için
	            bloomFilter[1] |= (1 << bitIndex3);         	
	           } else {
	            bloomFilter[0] |= (1 << bitIndex3);  // ilk byte'ta işlem yapar
	           }
			   
			   for (int i = 0; i < bloomFilter.length; i++) {
				textArea.append("Byte " + i + " : ");
				for (int j = 7; j >= 0; j--) {
					int bit = (bloomFilter[i] >> j) & 1;
					textArea.append(""+bit);
				}
				textArea.append("\n");
			}
			   textArea.setCaretPosition(textArea.getDocument().getLength());
			});

		cherry.addActionListener(e -> {
			textArea.append("Cherry is chosen.\n");
			   textArea.append("hash 1: "+hash1(8)+"\n");	
			   int bitIndex1 = hash1(8)% 8; // kaçıcı bit'teyim
	           
	           if (hash1(8) > 7) { // 2. byte değerlerini görmek için
	           	bloomFilter[1] |= (1 << bitIndex1);         	
	           } else {
	           	bloomFilter[0] |= (1 << bitIndex1);  // ilk byte'ta işlem yapar
	           } 
			   
			   textArea.append("hash 2: "+hash2(8)+"\n");
			   int bitIndex2 = hash2(8)% 8; // kaçıcı bit'teyim
	           
	           if (hash2(8) > 7) { // 2. byte değerlerini görmek için
	           	bloomFilter[1] |= (1 << bitIndex2);         	
	           } else {
	           	bloomFilter[0] |= (1 << bitIndex2);  // ilk byte'ta işlem yapar
	           }
	    	 		   
			   textArea.append("hash 3: "+hash3(8)+"\n");	   
			   int bitIndex3 = hash3(8)% 8; // kaçıcı bit'teyim
	            
	           if (hash3(8) > 7) { // 2. byte değerlerini görmek için
	            bloomFilter[1] |= (1 << bitIndex3);         	
	           } else {
	            bloomFilter[0] |= (1 << bitIndex3);  // ilk byte'ta işlem yapar
	           }
			   
			   for (int i = 0; i < bloomFilter.length; i++) {
				textArea.append("Byte " + i + " : ");
				for (int j = 7; j >= 0; j--) {
					int bit = (bloomFilter[i] >> j) & 1;
					textArea.append(""+bit);
				}
				textArea.append("\n");
			}
			   textArea.setCaretPosition(textArea.getDocument().getLength());
			});
		
		peach.addActionListener(e -> {
			textArea.append("Peach is chosen.\n");
			   textArea.append("hash 1: "+hash1(9)+"\n");	
			   int bitIndex1 = hash1(9)% 8; // kaçıcı bit'teyim
	           
	           if (hash1(9) > 7) { // 2. byte değerlerini görmek için
	           	bloomFilter[1] |= (1 << bitIndex1);         	
	           } else {
	           	bloomFilter[0] |= (1 << bitIndex1);  // ilk byte'ta işlem yapar
	           } 
			   
			   textArea.append("hash 2: "+hash2(9)+"\n");
			   int bitIndex2 = hash2(9)% 8; // kaçıcı bit'teyim
	           
	           if (hash2(9) > 7) { // 2. byte değerlerini görmek için
	           	bloomFilter[1] |= (1 << bitIndex2);         	
	           } else {
	           	bloomFilter[0] |= (1 << bitIndex2);  // ilk byte'ta işlem yapar
	           }
	    	 		   
			   textArea.append("hash 3: "+hash3(9)+"\n");	   
			   int bitIndex3 = hash3(1)% 8; // kaçıcı bit'teyim
	            
	           if (hash3(9) > 7) { // 2. byte değerlerini görmek için
	            bloomFilter[1] |= (1 << bitIndex3);         	
	           } else {
	            bloomFilter[0] |= (1 << bitIndex3);  // ilk byte'ta işlem yapar
	           }
			   
			   for (int i = 0; i < bloomFilter.length; i++) {
				textArea.append("Byte " + i + " : ");
				for (int j = 7; j >= 0; j--) {
					int bit = (bloomFilter[i] >> j) & 1;
					textArea.append(""+bit);
				}
				textArea.append("\n");
			}
			   textArea.setCaretPosition(textArea.getDocument().getLength());
			});

		pineapple.addActionListener(e -> {
			textArea.append("Pineapple is chosen.\n");
			   textArea.append("hash 1: "+hash1(10)+"\n");	
			   int bitIndex1 = hash1(10)% 8; // kaçıcı bit'teyim
	           
	           if (hash1(10) > 7) { // 2. byte değerlerini görmek için
	           	bloomFilter[1] |= (1 << bitIndex1);         	
	           } else {
	           	bloomFilter[0] |= (1 << bitIndex1);  // ilk byte'ta işlem yapar
	           } 
			   
			   textArea.append("hash 2: "+hash2(10)+"\n");
			   int bitIndex2 = hash2(10)% 8; // kaçıcı bit'teyim
	           
	           if (hash2(10) > 7) { // 2. byte değerlerini görmek için
	           	bloomFilter[1] |= (1 << bitIndex2);         	
	           } else {
	           	bloomFilter[0] |= (1 << bitIndex2);  // ilk byte'ta işlem yapar
	           }
	    	 		   
			   textArea.append("hash 3: "+hash3(10)+"\n");	   
			   int bitIndex3 = hash3(10)% 8; // kaçıcı bit'teyim
	            
	           if (hash3(10) > 7) { // 2. byte değerlerini görmek için
	            bloomFilter[1] |= (1 << bitIndex3);         	
	           } else {
	            bloomFilter[0] |= (1 << bitIndex3);  // ilk byte'ta işlem yapar
	           }
			   
			   for (int i = 0; i < bloomFilter.length; i++) {
				textArea.append("Byte " + i + " : ");
				for (int j = 7; j >= 0; j--) {
					int bit = (bloomFilter[i] >> j) & 1;
					textArea.append(""+bit);
				}
				textArea.append("\n");
			}
			   textArea.setCaretPosition(textArea.getDocument().getLength());
			});
		
		melon.addActionListener(e -> {
			textArea.append("Melon is chosen.\n");
			   textArea.append("hash 1: "+hash1(11)+"\n");	
			   int bitIndex1 = hash1(11)% 8; // kaçıcı bit'teyim
	           
	           if (hash1(11) > 7) { // 2. byte değerlerini görmek için
	           	bloomFilter[1] |= (1 << bitIndex1);         	
	           } else {
	           	bloomFilter[0] |= (1 << bitIndex1);  // ilk byte'ta işlem yapar
	           } 
			   
			   textArea.append("hash 2: "+hash2(11)+"\n");
			   int bitIndex2 = hash2(11)% 8; // kaçıcı bit'teyim
	           
	           if (hash2(11) > 7) { // 2. byte değerlerini görmek için
	           	bloomFilter[1] |= (1 << bitIndex2);         	
	           } else {
	           	bloomFilter[0] |= (1 << bitIndex2);  // ilk byte'ta işlem yapar
	           }
	    	 		   
			   textArea.append("hash 3: "+hash3(11)+"\n");	   
			   int bitIndex3 = hash3(11)% 8; // kaçıcı bit'teyim
	            
	           if (hash3(11) > 7) { // 2. byte değerlerini görmek için
	            bloomFilter[1] |= (1 << bitIndex3);         	
	           } else {
	            bloomFilter[0] |= (1 << bitIndex3);  // ilk byte'ta işlem yapar
	           }
			   
			   for (int i = 0; i < bloomFilter.length; i++) {
				textArea.append("Byte " + i + " : ");
				for (int j = 7; j >= 0; j--) {
					int bit = (bloomFilter[i] >> j) & 1;
					textArea.append(""+bit);
				}
				textArea.append("\n");
			}
			   textArea.setCaretPosition(textArea.getDocument().getLength());
			});

		check.addActionListener(e -> {
			textArea.append("Checking in progress...\n");
			textArea.setCaretPosition(textArea.getDocument().getLength());

		});
		reset.addActionListener(e -> {
			textArea.setText("");
			textArea.append("Reset is completed.\n");
			textArea.setCaretPosition(textArea.getDocument().getLength());
			textArea.append("There are no fruits chosen now.\n");
			for (int i = 0; i < bloomFilter.length; i++) {
				textArea.append("Byte " + i + " : ");
				for (int j = 7; j >= 0; j--) {
					bloomFilter[i] = 0b00000000;
					int bit = (bloomFilter[i] >> j) & 1;
					textArea.append("" + bit);
				}
				textArea.append("\n");
			}
			textArea.setCaretPosition(textArea.getDocument().getLength());
		});

		PLS.addActionListener(e -> {
			textArea.append("Randomly Selected\n");
			textArea.setCaretPosition(textArea.getDocument().getLength());

		});
	}
}
