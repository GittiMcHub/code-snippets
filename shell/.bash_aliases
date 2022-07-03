# General
alias ll='ls -lah'
alias e='echo'

# git 
alias gp='git pull'
alias gs='git status'
alias master='git checkout master && git pull'

# terraform
alias tf='terraform'
alias tfp='terraform plan'
alias tfa='terraform apply'

# Kubernetes
alias k='kubectl'
alias kgp='kubectl get pods'
alias kgv='kubectl get volumes'
alias wkgp='watch -n5 kubectl get pods'

# Keypairs | example: fingerprint ~/.ssh/id_rsa
alias fingerprint='ssh-keygen -l -v -f'
alias fingerprintmd5='ssh-keygen -l -E md5 -f'

webmTOmp4 () {
  ffmpeg -i "$1".webm -qscale 0 "$1".mp4
}    
mp4TOmp3 () {
  ffmpeg -i "$1".mp4 "$1".mp3
}