Biography 

The code helps to extract useful information from any biography text that has been given. Information extracted is as follow :

1. Person
2. Location
3. Organization
4. Date
5. Area of Interest (AOI)
6. Designation


Stanford NLP is used to extract first 4 parts and for rest Cortical IO is used.

A list of inbuilt desination and AOI is given. If a exact match is not found then Cortical IO is used to see the name noun (NN) term obtained is similar to terms in our list.
Using RMS score, it is determined which catergory the NN belongs to. 


===============================================================================================

To run the program :

You need to install SBT to run the code.

1. Download or clone the project.
2. Navigate to that location in terminal.
3. Run commands : 
	$ sbt compile							//This will compile the code and download the dependencies
	$ sbt run 								//This will run the code and at end you will see JSON input


===============================================================================================

Modification that you can make.

1. In file Biography.scala, uncomment the lines to get output in pretty format.
2. In file Biography.scala, you can give any input string.
3. If you want to experiment with accuracy
	a. add more designation and Area of Interest in CorticalExt.scala line 13,14,15,16 List. More the number, better the accuracy. 
	b. In file CorticalExt.scala line 39, change increase 0.12. It will give better accuracy but might miss detecting some terms. Decreasing 0.12 will give more false positives.
	