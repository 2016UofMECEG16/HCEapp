
# HCE App #

- The HCE app is based on and extracted  the Android developer Card Emulation  API that can be found here: https://github.com/googlesamples/android-CardEmulation

- The HCE app incorporates NFC card reading functionality and uses Ralf Wondratschek's implementation that can be found here:
http://code.tutsplus.com/tutorials/reading-nfc-tags-with-android--mobile-17278

- The HCE app incorporates NFC card writing functionality. The writing functionality used is extracted from:
https://www.youtube.com/watch?v=aiSHHj7jWpQ

# HCE Development #

- Android Studio IDE is used in developing the HCE app

- Java is the programming language used

- Throughout the testing, Nexus 6P and Nexus 5 smartphones were used along with the Raspberry Pi 1 B+ interfaced with the NXP OM5577 NFC controller.

# How to use the HCE App #

## Card is the activated E-Pass ##

- Initially, when the is first opened, the card is assumed to be the active E-Pass.
- 
  ### Activating the smartphone and deactivating the card ###

  * __STEPS:__
        * Scan the NFC card to the phone
    
        * The ***"PHONE"*** button should be enabled.
    
        * Press ***"PHONE"*** wait unitl the pop-up says, *"Tap card to the phone"*
  
        * Tap the card, it will say that the card is being written to. Wait until the pop-up that says *"E-Pass succesfully      transferred to phone"* fade then remove the card.
  
  - __NOTE:__ At this point, the smartphone is the active E-Pass and ***"PHONE"*** button is disabled and the following buttons are enabled:
  
        * ***"CARD"***: activates the card and deactivates the phone as the active E-Pass
  
        * ***"CHECK"***: displays the user's existing balance
  
        * ***"TIME"***: displays the user's time transfer information
    
        * ***"$-20"***, ***"$-5"***, ***"$5"***, ***"$20"***: displays the amount the user specifies (i.e. pressing ***"$5"*** outputs 5 then pressing ***"$20"*** outputs "$25")
    
        * ***"ADD"***:  adds the specified amount by the user to his/her existing balance
  
  ### Scanning the smartphone to the scanner ###
  
  * The scanner will see the smartphone as the valid E-Pass and will light the green LED
  
  * Scanning the card will cause the scanner to light the red LED since it is invalid
  
  * There might be inconsistencies with the detection of the phone by the scanner due to the NFC chip placement in the phone. In this case the scanner lights the red LED, which means the transaction was not successful and requires the user to re-scan the smartphone.
  
## Smartphone is the activated E-Pass ##

  - This is the instance when the active E-Pass is the smartphone
  
  ### Activating the card and deactivating the smartphone ###

  * __STEPS:__
  
      * Press ***"CARD"*** wait unitl the pop-up says, *"Tap card to the phone"*
  
      * Tap the card, it will say that the card is being written to. Wait until the pop-up that says *"E-Pass succesfully      transferred to card"* fade then remove the card.
  
  * __NOTE:__ At this point, the card is the active E-Pass and the following buttons are disabled:
  
      * ***"$-20"***, ***"$-5"***, ***"$5"***, ***"$20"***
  
      * ***"ADD"***
  
  The ***"PHONE"*** button remains disabled and and the ***"CHECK"*** and ***"TIME"*** buttons remain enabled.


