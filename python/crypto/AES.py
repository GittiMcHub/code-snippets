from Crypto.Cipher import AES
from Crypto import Random
import sys
import time


#
#   Return Codes
#        1 = Failed to encrypt and write the output file
#       10 = Not enough or to many parameters
#       11 = Could not open input file

if len(sys.argv) == 1 or sys.argv[1] == 'help' or sys.argv[1] == '?':
    print 'Usage:'
    print 'This script requires 1 parameter: < inputfile >\n'
    print 'Return Codes'
    print ' 1 = Failed to en or decrypt'
    print '10 = To many parameters'
    print '11 = Could not open input file'
    exit(0)

if len(sys.argv) != 2:
    print 'This script requires 1 parameter'
    exit(10)

try:
    input_file = open(sys.argv[1], "r")
except:
    print 'Could not open file: ' + sys.argv[1]
    exit(11)

# End Parameter handling

cbc_key = Random.get_random_bytes(16)

iv = Random.get_random_bytes(16)

aes1 = AES.new(cbc_key, AES.MODE_CBC, iv)
aes2 = AES.new(cbc_key, AES.MODE_CBC, iv)

plain_text = ''
with input_file as f:
    for line in f:
        plain_text += line

while len(plain_text) % 16 != 0:
    plain_text += '\x00'

# CAPTURE TIME FOR ENCRYPTION
start = time.time()
cipher_text = aes1.encrypt(plain_text)
end = str(time.time() - start)

# print('[+] ENCRYPTION TIME: ' + end)
with open('aes_time.enc', 'a') as f:
    f.write(end + '\n')

# CAPTURE TIME FOR DECRYPTION
start = time.time()
msg = aes2.decrypt(cipher_text)
end = str(time.time() - start)

# STRIP TRAILING NULL BYTES (PADDING)
msg = msg.rstrip('\x00')

# print 'msg equal: ' + str(msg == plain_text)
# print('[+] DECRYPTION TIME: ' + end)
with open('aes_time.dec', 'a') as f:
    f.write(end + '\n')
