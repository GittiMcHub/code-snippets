import hashlib
import time
import sys

# initializing string
# str = "SHA1 Clear text"

plain_text = ''
input_text = sys.argv[1]
with open(input_text, 'r') as f:
    for line in f:
        plain_text += line

start = time.time()
result = hashlib.sha1(input_text.encode())
end = time.time() - start

with open('sha1_' + input_text[11:] + '.hash', 'a') as f:
    f.write(str(end) + '\n')

# printing the equivalent hexadecimal value.
print("The hexadecimal equivalent of SHA1 is : ")
print(result.hexdigest())
