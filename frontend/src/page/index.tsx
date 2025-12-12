import { useState } from "react"

function App() {

    const [text, setText] = useState<string>('');
    const [data, setData] = useState();
    const [notification, setNotification] = useState<string>('');

    const handleClickSend = async () => {
        try {
            const res = await fetch('http://localhost:5000/api/test/value', {
                method: 'POST',
                headers: {
                    'Content-type': 'application/json',
                },
                body: JSON.stringify(data),
            });
            setNotification(String(await res.json()));
        } catch (e) {
            setNotification(String(e));
        }
    }

    const handleClickGet = async () => {
        try {
            const res = await fetch('http://localhost:5000/api/test/value');
            if (res.status === 200) {
                setData(await res.json());
                return;
            }
            setNotification(await res.json());
        } catch (e) {
            setNotification(String(e));
        }
    }

    return (
        <div className="ml-4">
            <div className="my-5">
                <h1>Hello world!</h1>
                <input
                    type="text"
                    value={text}
                    onChange={e => setText(e.target.value)}
                    className="p-2 border-2 border-black focus:outline-none"
                />
                <button
                    onClick={handleClickSend}
                    className="ml-5 border-2 border-black bg-green-500 p-2 cursor-pointer"
                >
                    Send data
                </button>
            </div>
            <hr />
            <div className="my-5">

                <button
                    onClick={handleClickGet}
                    className="ml-5 border-2 border-black bg-green-500 p-2 cursor-pointer"
                >
                    Get text
                </button>

                <h1>From backend: {data}</h1>

            </div>

            <div className="my-5">
                <h2>{notification}</h2>
            </div>
        </div>
    )
}

export const Component = App;
