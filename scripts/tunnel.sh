 #!/bin/sh
 ## Bridge the SQL server from the remote machine to the local one
 ssh -NL 5432:localhost:5432 35.227.36.41 & 
