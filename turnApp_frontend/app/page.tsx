import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"

export default function LoginPage() {
  return (
    <div className="min-h-screen bg-[#2a2a2a] flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="bg-white rounded-2xl p-8 shadow-lg">
          {/* Logo */}
          <div className="flex flex-col items-center mb-8">
            <div className="w-32 h-32 bg-gray-200 rounded-full mb-4" />
            <h1 className="text-2xl font-bold text-foreground">TURN-APP</h1>
            <p className="text-sm text-muted-foreground">Gestión de turnos</p>
          </div>

          {/* Login Form */}
          <div className="space-y-6">
            <div className="text-center mb-6">
              <h2 className="text-xl font-semibold text-foreground">Iniciar sesión</h2>
            </div>

            <div className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="email" className="text-sm font-medium text-foreground">
                  Correo electrónico
                </Label>
                <Input id="email" type="email" placeholder="" className="h-11 bg-gray-100 border-0" />
              </div>

              <div className="space-y-2">
                <Label htmlFor="password" className="text-sm font-medium text-foreground">
                  Contraseña
                </Label>
                <Input id="password" type="password" placeholder="" className="h-11 bg-gray-100 border-0" />
              </div>

              <Link href="/admin">
                <Button className="w-full h-11 bg-black hover:bg-black/90 text-white font-medium">
                  Iniciar sesión
                </Button>
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
