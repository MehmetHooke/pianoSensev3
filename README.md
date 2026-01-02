# 🎶 PianoSense v3

**PianoSense v3** is a mobile application developed with **Kotlin** for Android, using **JavaScript for audio analysis**, **TarsosDSP** for sound processing, and **Firebase** as the backend service.  
It allows users to log in, play or upload music, and see real-time analysis results stored and synchronized through Firebase.

---

## 🚀 Features

- 📱 **User Authentication** – Sign up / Login / Logout with Firebase Auth  
- 🎵 **Music Management** – Songs are stored in **Firebase Firestore** and retrieved dynamically  
- 🎧 **Audio Analysis** – Built with **TarsosDSP** and **JavaScript**, detecting pitch and comparing notes  
- 🔄 **Real-time Results** – Analysis data sent and updated via **Firebase Realtime Database**  
- ☁️ **Cloud Integration** – Full use of Firebase ecosystem for storage, auth, and database  

---

## 🛠️ Technologies Used

- **Kotlin** – Core Android app development  
- **JavaScript** – Audio processing & analysis logic  
- **TarsosDSP** – Digital signal processing library for sound analysis  
- **Firebase Authentication** – User login/registration  
- **Firebase Firestore** – Music file storage and retrieval  
- **Firebase Realtime Database** – Storing and syncing analysis results in real time  

---

## 📂 Project Structure

pianosensev3/

│── app/ # Kotlin Android codebase

│── js/ # JavaScript audio analysis logic

│── firebase/ # Firebase integration (Auth, Firestore, RealtimeDB)

│── README.md # Project documentation



---

## 📸 Example Use Case

1. User logs in via Firebase Auth.  
2. Selects or uploads a song (stored in Firestore).  
3. Song is analyzed with **TarsosDSP** + JS (pitch detection & note matching).  
4. Analysis results are sent to **Realtime Database**.  
5. User sees real-time feedback in the app.  

---

## 🌍 Future Improvements

- 🎹 Add a **visual piano roll** to display detected notes in real time  
- 📊 Integrate **analytics dashboard** with Power BI or custom charts  
- 📱 Expand support to iOS (React Native / Flutter)  
- 🤖 AI-based enhancements for more accurate note detection  

---

## 📬 Contact

👤 **Mehmet HÖKE**  
- 📧 Email: [hoke628@gmail.com](mailto:hoke628@gmail.com)  
- 💼 LinkedIn: [Mehmet HÖKE](https://www.linkedin.com/in/mehmethoke/)  
- 💻 GitHub: [MehmetHooke](https://github.com/MehmetHooke)  

---

⭐ *If you like this project, please give it a **star** on GitHub!*
