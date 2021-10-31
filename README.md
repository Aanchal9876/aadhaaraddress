# Aadhaar Address Updation

This is the first app we made for the UIDAI Hackathon 2021, which was based on the problem statement 1, Address Updation using Landlords Address </br>

Flow of the application:
1. The user is first asked his/her Aadhar number for authentication.</br>
2. If the OTP is sent (using Aadhar OTP API), user is directed to the second screen, where they are supposed to enter the OTP.</br>
3. After the OTP Verification user is then directed to the Dashboard, where they are provided with 3 Options</br>
    3.1 *Apply for Address Change* : Here the user can make a new application for aadhar address updation. They have to 
                                     enter their land lords/ providers aadhar number and then application prompts them
                                     to check if they have entered the correct number.
                                     
    3.2 *Check Applicaton Status* : Here the user can check whether the landlord has accepted their request or not or whether the user has
                                    made any requests till now. If the landlord has agreed to share address then user is prompted to next screen 
                                    where they can make minor changes to the address and then procees to GPS verification using Geocoding API(Position Stack).
                                    Then the required changes are made to the database.
                                    
    3.3 *Check Application Requests* : This screen is specifically for the landlord. Now here, if the landlord agrees the share his aadhar address, then he is directed to 
                                       OTP verification for downloading e-KYC. After which, the user is shown his address with name his photo and then he can make the final decision.
                                       
                           
It is a Android native application based on Kotlin. We have used Firebase for database.
The applications are completely auditable and logs can be made from database whenever required.

We hope you like our application :). It was a great learning experience for us and we came to know about how actually UIDAI might work.

[![Watch our presentation](https://img.youtube.com/vi/Ds3qqZuB36A/default.jpg)](https://youtu.be/Ds3qqZuB36A)</br>
[APK FILE] (https://drive.google.com/file/d/1pxFaNfpFj4g4v-3aZpLty9ber1--g5gy/view?usp=sharing)
