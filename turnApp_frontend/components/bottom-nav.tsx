import Link from "next/link"
import { Home, Calendar, Users, BarChart3, Bell, User, Clock } from "lucide-react"
import { cn } from "@/lib/utils"

interface BottomNavProps {
  activeTab: string
  role: "admin" | "empleado"
}

export function BottomNav({ activeTab, role }: BottomNavProps) {
  const adminTabs = [
  { id: "inicio", label: "Inicio", icon: Home, href: "/admin" },
  { id: "horarios", label: "Horarios", icon: Calendar, href: "/admin/horarios" },
  { id: "empleados", label: "Empleados", icon: Users, href: "/admin/empleados" },
  { id: "estadisticas", label: "Estadísticas", icon: BarChart3, href: "/admin/estadisticas" },
  { id: "avisos", label: "Avisos", icon: Bell, href: "/admin/avisos" },
  { id: "perfil", label: "Perfil", icon: User, href: "/admin/perfil" },
  ]

  const empleadoTabs = [
    { id: "inicio", label: "Inicio", icon: Home, href: "/empleado" },
    { id: "horarios", label: "Horarios", icon: Calendar, href: "/empleado/horarios" },
    { id: "asistencia", label: "Asistencia", icon: Clock, href: "/empleado/asistencia" },
    { id: "avisos", label: "Avisos", icon: Bell, href: "/empleado/avisos" },
    { id: "perfil", label: "Perfil", icon: User, href: "/empleado/perfil" },
  ]

  const tabs = role === "admin" ? adminTabs : empleadoTabs

  return (
    <nav className="fixed bottom-0 left-0 right-0 bg-white border-t border-border">
      <div className="max-w-md mx-auto px-2 py-2">
        <div className="flex items-center justify-around">
          {tabs.map((tab) => {
            const Icon = tab.icon
            const isActive = activeTab === tab.id

            return (
              <Link
                key={tab.id}
                href={tab.href}
                className={cn(
                  "flex flex-col items-center gap-1 px-3 py-2 rounded-lg transition-colors min-w-[60px]",
                  isActive ? "text-foreground" : "text-muted-foreground",
                )}
              >
                <Icon className="w-6 h-6" />
                <span className="text-[10px] font-medium">{tab.label}</span>
              </Link>
            )
          })}
        </div>
      </div>
    </nav>
  )
}
