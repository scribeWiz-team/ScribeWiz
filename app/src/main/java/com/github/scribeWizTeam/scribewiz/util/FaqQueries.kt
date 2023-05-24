package com.github.scribeWizTeam.scribewiz.util

class FaqQueries {
    companion object {
        val faqs: Map<String, String>
            get() = mapOf(
                "How to log in with Google?" to
                        "To log in with Google, select 'Sign in with Google' on the login screen or from the profile screen. " +
                        "Please note that you must have an existing Google account to use this feature.",

                "Can I use the app offline?" to
                        "Yes, you can use ScribeWiz offline. However, you won't be able to sync transcriptions from the cloud or " +
                        "participate in social features, such as challenges or sharing transcriptions with friends. " +
                        "You can still create new transcriptions and view existing ones.",

                "Can I use any instrument for the transcriptions?" to
                        "ScribeWiz is designed primarily for transcribing melodies, so it may be better suited for instruments like " +
                        "voice, flute, violin, bass, etc. It has limitations in transcribing chords and it's not recommended for " +
                        "percussion instruments like drums.",

                "What are musicxml files?" to
                        "MusicXML is an open interchange format for sharing music notation data among multiple software platforms. " +
                        "This flexible format uses text to represent musical elements and can be read by both machines and humans.",

                "Getting started?" to
                        "To get started, grab your instrument and navigate to the recording screen. Configure the settings for the " +
                        "recording and click next. Grant the app microphone permissions and start playing your instrument. " +
                        "Once you're done, press stop and the app will automatically transcribe your recording into sheet music " +
                        "which you can find in the library screen. You can view and edit the transcription by tapping on it.",

                "How can I use the in-app metronome?" to
                        "You can turn on the metronome while recording by enabling it on the recording screen. The metronome will " +
                        "start playing as soon as the recording starts and will stop when the recording stops.",

                "Can I change the tempo of the metronome?" to
                        "The tempo of the metronome matches the tempo of the transcription, so you can change this in the recording settings.",

                "How can I change the time signature, tonality, staff key, or tempo of the transcription?" to
                        "These settings can be adjusted from the recording screen before you start the transcription.",

                "I accidentally declined permission for the microphone, how can I grant permission again?" to
                        "You can re-grant permission from the recording screen. If this doesn't work, try closing the app and retrying.",

                "How do I share my transcriptions with friends on the app?" to
                        "To share your transcriptions, navigate to your library, and tap on the share button next to the piece you " +
                        "want to share. You can then choose the friend you want to share it with.",

                "How can I export my transcriptions to other apps?" to
                        "To export your transcriptions, long-press on the piece you want to export in your library. If you are logged in " +
                        "with Google, you should see options for apps you can choose to export to, such as Gmail, Messages, Google Drive, etc.",

                "How do I delete a transcription?" to
                        "To delete a transcription, navigate to your library and swipe left on the song you want to delete.",

                "How do I rename a transcription?" to
                        "To rename a transcription, navigate to your library and long-press on the piece you want to rename.",

                "How can I edit my piece" to
                        "To edit a piece, tap on it from the library. You can then scroll to the position of the note you want to change and " +
                        "select the new note from the menu. After this, press the 'Replace note' button to replace the note.",

                "How can I log out?" to
                        "To log out, go to your profile page and tap on the 'Log out' button.",

                "How do I send invites to my friends and check my friends list?" to
                        "To send an invite, go to your profile page, type your friend's username, and press 'Send Invite'. Once your friend accepts your " +
                        "invitation, they will appear in your friends list at the bottom of your profile page.",

                "How can I participate in a challenge?" to
                        "To participate in a challenge, long-press on one of your transcriptions and tap on the 'Submit to Challenge' option. " +
                        "You can then select which challenge you want to participate in and your piece will be submitted automatically.",

                "How do I vote for songs in a challenge?" to
                        "To vote for a song in a challenge, go to the challenges page, find the song you want to vote for, and tap on the 'Vote' button.",

                "How do I see the results of a challenge?" to
                        "Once a challenge is over, if you've won, a badge will appear on your profile page.",

                "How can I report bugs or request help with the app?" to
                        "If you encounter a bug or need assistance with ScribeWiz, please visit our GitHub page at https://github.com/scribeWiz-team/ScribeWiz " +
                        "and create a new issue in the 'Issues' tab.",

                "How can I contact the developers?" to
                        "As of now, direct contact with the developers is not available. For any assistance, please use the 'Issues' tab on our GitHub page.",

                "How can I reset my password?" to
                        "Currently, ScribeWiz uses Google Sign In, so there's no separate password for the app itself. If you need to reset your Google password, " +
                        "please follow Google's password recovery process.",

                "What kind of audio quality do I need for accurate transcriptions?" to
                        "ScribeWiz works best with clear, high-quality audio. If the audio is too quiet, has a lot of background noise, or is of poor quality, " +
                        "the transcription may not be accurate.",

                "How long does the transcription process take?" to
                        "The transcription process duration depends on the length of your recording. Typically, it is quite fast.",

                "Can I transcribe an already existing audio file?" to
                        "Currently, ScribeWiz supports transcriptions from direct recordings within the app. Transcribing from an existing audio file isn't " +
                        "supported yet, but we're working on it!",

                "How do I update my profile information?" to
                        "Your profile information is linked to your Google account. Any changes to your profile should be done through Google.",

                "Can I use ScribeWiz on multiple devices?" to
                        "Yes, as long as you sign in with the same Google account, your transcriptions will be synced across devices.",

                "What should I do if my app crashes?" to
                        "If ScribeWiz crashes, try restarting the app. If the issue persists, consider reinstalling the app. Please report persistent issues on our GitHub page.",

                "Is there a limit on the number of transcriptions I can make?" to
                        "There is no set limit on the number of transcriptions you can make. However, storage restrictions may apply based on your device or Google account."
            )
    }
}