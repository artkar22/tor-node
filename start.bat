@echo off

echo Podaj rozmiar sieci
echo Node
set/p "Node=>>"

for /L %%A in (1,1,%Node%) do (
	START Onion.jar %%A 0
	)





