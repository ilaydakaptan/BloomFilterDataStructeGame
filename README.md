﻿# BloomFilterDataStructeGame

A Bloom Filter is a data structure used to quickly check whether an element is part of a set. It is memory-efficient but allows for false positives, meaning it can sometimes indicate that an element is in the set when it is not. However, it never produces false negatives, meaning if it says an element is definitely not in the set, it is correct.

Simple Working Principle of Bloom Filter:
-You start with an empty bit array (e.g., an array of fixed size, initialized with 0s).

-When you want to add an element, you pass it through a series of hash functions. These hash functions compute several different positions in the bit array.

-Each time an element is added, the positions calculated by the hash functions are set to 1.

-To check if an element is in the set, the same hash functions are applied to the element. If all the positions in the bit array are 1, the element is probably in the set. If any of the positions is 0, the element is definitely not in the set.

-----
Bloom Filter, bir öğenin bir koleksiyonda olup olmadığını hızlıca kontrol etmek için kullanılan bir veri yapısıdır. Hafızayı verimli kullanır ve yanlış pozitif sonuçlar döndürebilir, ancak yanlış negatif sonuçlar vermez. Yani, bir öğe için "bu kesinlikle yok" diyorsa doğru sonuçtur, ancak "bu var" diyorsa, bazen yanlış olabilir (varmış gibi gösterip aslında olmayabilir).

Bloom Filter'ın Basit Çalışma Prensibi:
-Boş bir bit dizisi (örneğin, 0'lardan oluşan sabit uzunlukta bir dizi) ile başlarsınız.

-Bir hash fonksiyonu seti kullanarak, eklemek istediğiniz her öğe için birkaç farklı pozisyon hesaplanır. Bu hash fonksiyonları, her öğeyi dizinin farklı pozisyonlarına yönlendirir.

-Bir öğe eklendiğinde, o öğenin hash fonksiyonları tarafından verilen dizideki pozisyonlar 1 olarak işaretlenir.

-Bir öğeyi kontrol ederken, yine aynı hash fonksiyonları çalıştırılır ve o öğeye ait pozisyonlardaki bitlerin tamamı 1 ise, öğe muhtemelen vardır. Eğer herhangi biri 0 ise, öğe kesinlikle yoktur.
