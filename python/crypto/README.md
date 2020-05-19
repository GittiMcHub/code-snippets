# Crypto Code Snippets

Created during COMP4337 Course "Securing Fixed and Wireless Networks" at UNSW Sydney.

For that task we had to compare the different Crypto Algorithms. (Lecture content and figures not included here.

# Comparisions:
## Comparison: DES encryption vs. AES encryption
DES and AES encryption are using the same API. For both you need a Key and an Initial Vector. DES only facilitates plaintext blocks of length 64-bits compared to AES being able to compute with 128, 192 and 256-bit blocks. In computing DES, the plaintext is split into two for encryption, whereas AES will process the entire block. DES encryption utilises the Feistel cipher structure compared to AES using the substitution-permutation network making AES the faster algorithm.
As described in the lecture (WK01-03 Slide 37) we expected AES being 6 times faster than DES, but in our time measurements we identified only an average of 1.66x faster encryption performance comparing their minimum encryption time. Even with serval timing captures, we could not reach the excepted performance. After some research, we found out that AES is 6 times faster than Tripple-DES (https://www.educba.com/des-vs-aes/)


## Comparison: DES encryption vs. RSA encryption
DES encryption is using symmetric key cryptography whereas, RSA uses asymmetric key cryptography involving a private and public key. The calculation of DES encryption works on the Feistel cipher structure over 16 rounds compared to RSA which is comprises its encryption off two large prime numbers, making it computationally slower than DES.


## Comparison: DES encryption vs. SHA-1 digest generation
DES is a symmetric key cryptography algorithm whereas SHA-1 is a cryptographic hash function I.e. it isn’t decryptable. DES takes a block size of 64-bits compared to SHA-1 using 512-bit input and giving a 160-bit hash.


## Comparison: HMAC signature generations vs. SHA-1 digest generation
HMAC is a hash-based message authentication code which is calculated with a cryptographic hash function like SHA-1. In our case the we used the default hash function, which is MD5. 


## Comparison: RSA encryption and decryption times
As shown in figure “(iii) RSA encryption / decryption times” The encryption in RSA is much faster than decryption. Given the example on WK-02-02 Slide 22: to encrypt a message we use [ c = m^e mod n ]. For decryption we use [ m = c^d mod n ]. 
To explain why decryption is much slower than encryption, we have to focus on the exponents **e** and **d**. 
**e** must be less than n, where n is the result out of **[ pq ]** and **p** and **q** are very large prime numbers.
**d** must be greater than **e**, because **[ d = ed-1 ]**, which must be dividable by **z**. 
**z** on the other hand is the result out of **[ (p-1)(q-1) ]**.
In the end, **e** is a lower exponent than **d**, and therefore the CPU must do much more multiplications to decrypt a message.
