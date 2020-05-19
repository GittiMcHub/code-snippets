# Following code reads its source file and computes an HMAC signature for it:
import hashlib
import hmac
import sys
import time

digest_maker = hmac.new(key='secret-shared-key-goes-here', digestmod=hashlib.sha1)

# f = open('lorem.txt', 'rb')
f = open(sys.argv[1], 'rb')

start = time.time()
try:
    while True:
        block = f.read(1024)
        if not block:
            break
        digest_maker.update(block)
finally:
    f.close()

digest = digest_maker.hexdigest()
end = time.time() - start

with open('HMAC_' + sys.argv[1][11:] + '.hash', 'a') as f:
    f.write(str(end) + '\n')

print digest
