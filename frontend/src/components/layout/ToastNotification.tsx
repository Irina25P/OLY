import { toast } from 'react-hot-toast';

export const showNotification = (message: string, title: string) => {
    toast.custom((t) => (
        <div
            className={`${
                t.visible ? 'animate-enter' : 'animate-leave'
            } max-w-md w-full bg-white shadow-2xl rounded-xl pointer-events-auto flex ring-1 ring-black ring-opacity-5 border-l-4 border-primary`}
        >
            <div className="flex-1 w-0 p-4">
                <div className="flex items-start">
                    <div className="ml-3 flex-1">
                        <p className="text-sm font-bold text-primary uppercase tracking-wide">
                            {title}
                        </p>
                        <p className="mt-1 text-sm text-gray-600 leading-relaxed">
                            {message}
                        </p>
                    </div>
                </div>
            </div>
            <div className="flex border-l border-gray-100">
                <button
                    onClick={() => toast.dismiss(t.id)}
                    className="w-full border border-transparent rounded-none rounded-r-lg p-4 flex items-center justify-center text-sm font-bold text-accent hover:text-secondary transition-colors duration-200 focus:outline-none"
                >
                    OK
                </button>
            </div>
        </div>
    ), {
        duration: 7000, // 7 secunde
        position: 'top-right',
    });
};
