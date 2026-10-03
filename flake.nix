{
  description = "Additional Error Prone checks for tidier Java code";

  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";

  outputs =
    { nixpkgs, ... }:
    let
      forAllSystems = nixpkgs.lib.genAttrs [
        "aarch64-darwin"
        "aarch64-linux"
        "x86_64-linux"
      ];
    in
    {
      devShells = forAllSystems (
        system:
        let
          pkgs = nixpkgs.legacyPackages.${system};
          jdk = pkgs.jdk25;
        in
        {
          default = pkgs.mkShell {
            packages = [
              jdk
              (pkgs.maven.override { jdk_headless = jdk; })
            ];
            JAVA_HOME = jdk.home;
          };
        }
      );
    };
}
