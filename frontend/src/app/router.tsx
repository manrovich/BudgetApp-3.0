import { createBrowserRouter } from "react-router-dom";
import { ROUTES } from "@/shared/model/routes";
import { App } from "./app";

export const router = createBrowserRouter([
    {
        element: <App />,
        children: [
            {
                path: ROUTES.HOME,
                lazy: () => import('@/page/index'),
            }
        ]
    }
]);