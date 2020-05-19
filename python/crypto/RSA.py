import Crypto
from Crypto.PublicKey import RSA
from Crypto import Random
import ast
import time
import sys

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

random_generator = Random.new().read
key = RSA.generate(1024, random_generator)

publickey = key.publickey()

plain_text = ''
input_text = sys.argv[1]
with input_file as f:
    for line in f:
        plain_text += line

encrypted = []

# CAPTURE TIME FOR ENCRYPTION
start = time.time()
while len(plain_text) > 0:
    encrypted.append(publickey.encrypt(plain_text[:128], 128))
    plain_text = plain_text[128:]
end = str(time.time() - start)

# print('[+] ENCRYPTION TIME: ' + end)
with open('rsa_time.enc', 'a') as f:
    f.write(end + '\n')

decrypted = []
# CAPTURE TIME FOR DECRYPTION
start = time.time()
for i in range(len(encrypted)):
    decrypted.append(key.decrypt(ast.literal_eval(str(encrypted[i]))))
end = str(time.time() - start)

# print('[+] DECRYPTION TIME: ' + end)
with open('rsa_time.dec', 'a') as f:
    f.write(end + '\n')
