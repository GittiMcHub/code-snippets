from Crypto.Cipher import DES
from Crypto import Random
import sys
import time


#
#   Return Codes
#        1 = Failed to encrypt and write the output file
#       10 = Not enough parameters
#       11 = IV Parameter does not contain valid Hex digits
#       12 = Key Parameter does not contain valid Hex digits
#       13 = Could not open input file
#       14 = Could not open output file
if len(sys.argv) == 1 or sys.argv[1] == 'help' or sys.argv[1] == '?':
    print 'Usage:'
    print 'This script requires 4 parameters: < iv > < key > < inputfile > < outputfile >\n'
    print 'Return Codes'
    print ' 1 = Failed to encrypt and write the output file'
    print '10 = Not enough parameters'
    print '11 = IV Parameter does not contain valid Hex digits'
    print '12 = Key Parameter does not contain valid Hex digits'
    print '13 = Could not open input file'
    print '14 = Could not open output file'
    exit(0)

if len(sys.argv) != 5:
    print 'This script requires 4 parameters (has ' + str(len(sys.argv)-1) + ': < iv > < key > < inputfile > < outputfile >'
    exit(10)

try:
    iv = sys.argv[1]
    int(iv, 16)  # fails if not a valid hex
except:
    print 'IV value must be a string comprised only of HEX digits. (e.g. fedcba9876543210)'
    exit(11)

try:
    cbc_key = sys.argv[2]
    int(cbc_key, 16)  # fails if not a valid hex
except:
    print 'Key value must be a string comprised only of HEX digits. (e.g. 40fedf386da13d57)'
    exit(12)

try:
    input_file = open(sys.argv[3], "r")
except:
    print 'Could not open file: ' + sys.argv[3]
    exit(13)

try:
    output_file_name = sys.argv[4]
    ftest = open(sys.argv[4], "w")
    ftest.close()
except:
    print 'Could not open file: ' + sys.argv[4]
    exit(14)

# End Parameter handling

hex_cbc_key = ''
j = 0
for i in range(0, len(cbc_key), 2):
    # CONVERT THE HEX STRING INTO HEX BYTES
    hex_cbc_key += chr(int(cbc_key[i:i+2], 16))
    j += 1


hex_iv = ''
j = 0
for i in range(0, len(iv), 2):
    # CONVERT THE HEX STRING INTO HEX BYTES
    hex_iv += chr(int(iv[i:i+2], 16))
    j += 1

# CREATE A DES CBC CIPHER OBJECT FOR ENCRYPTION
des1 = DES.new(hex_cbc_key, DES.MODE_CBC, hex_iv)
# CREATE A DES CBC CIPHER OBJECT FOR DECRYPTION
des2 = DES.new(hex_cbc_key, DES.MODE_CBC, hex_iv)


plain_text = ''
with input_file as f:
    # READ EACH LINE OF INPUT TEXT AND APPEND TO PLAIN_TEXT
    for line in f:
        plain_text += line

# CHECK IF LENGTH IS A MULTIPLE OF 8
while len(plain_text) % 8 != 0:
    plain_text += '\x00'

# CAPTURE TIME FOR ENCRYPTION
start = time.time()
cipher_text = des1.encrypt(plain_text)
end = str(time.time() - start)

# print('[+] ENCRYPTION TIME: ' + end)
with open('des_time.enc', 'a') as f:
    f.write(end + '\n')

# CAPTURE TIME FOR DECRYPTION
start = time.time()
msg = des2.decrypt(cipher_text)
end = str(time.time() - start)

# print('[+] DECRYPTION TIME: ' + end)
with open('des_time.dec', 'a') as f:
    f.write(end + '\n')

# STRIP TRAILING NULL BYTES (PADDING)
msg = msg.rstrip('\x00')


with open(output_file_name + '.txt', 'w') as f:
    f.write(msg)

with open(output_file_name, 'w') as f:
    f.write(cipher_text)
